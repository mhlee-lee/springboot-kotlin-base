package org.test.kotlin_base.infrastructure.database.configuration

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.Option
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder
import org.springframework.boot.r2dbc.autoconfigure.R2dbcProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.support.DefaultTransactionDefinition
import reactor.core.publisher.Mono

enum class DatabaseRoute {
    WRITE,
    READ,
}

object DatabaseRouteContext {
    const val KEY = "databaseRoute"
}

private class RoutingConnectionFactory(
    writeConnectionFactory: ConnectionFactory,
    readConnectionFactory: ConnectionFactory,
) : AbstractRoutingConnectionFactory() {
    init {
        setLenientFallback(false)
        setDefaultTargetConnectionFactory(writeConnectionFactory)
        setTargetConnectionFactories(
            mapOf(
                DatabaseRoute.WRITE to writeConnectionFactory,
                DatabaseRoute.READ to readConnectionFactory,
            )
        )
    }

    override fun determineCurrentLookupKey(): Mono<Any> {
        return Mono.deferContextual { context ->
            val route = if (context.hasKey(DatabaseRouteContext.KEY)) {
                context.get(DatabaseRouteContext.KEY)
            } else {
                DatabaseRoute.WRITE
            }
            Mono.just(route)
        }
    }
}

@Configuration
@EnableR2dbcRepositories(basePackages = ["org.test.kotlin_base.domain"])
class DataSourceConfig {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean("writeR2dbcProperties")
    @ConfigurationProperties(prefix = "app.r2dbc.write")
    fun writeR2dbcProperties(): R2dbcProperties = R2dbcProperties()

    @Bean("readR2dbcProperties")
    @ConfigurationProperties(prefix = "app.r2dbc.read")
    fun readR2dbcProperties(): R2dbcProperties = R2dbcProperties()

    @Bean("writeConnectionFactory")
    fun writeConnectionFactory(
        @Qualifier("writeR2dbcProperties") properties: R2dbcProperties,
    ): ConnectionFactory {
        return createConnectionFactory("write", properties)
    }

    @Bean("readConnectionFactory")
    fun readConnectionFactory(
        @Qualifier("readR2dbcProperties") properties: R2dbcProperties,
    ): ConnectionFactory {
        return createConnectionFactory("read", properties)
    }

    @Bean
    @Primary
    fun connectionFactory(
        writeConnectionFactory: ConnectionFactory,
        readConnectionFactory: ConnectionFactory,
    ): ConnectionFactory {
        return RoutingConnectionFactory(
            writeConnectionFactory = writeConnectionFactory,
            readConnectionFactory = readConnectionFactory,
        )
    }

    @Bean
    fun connectionFactoryInitializer(writeConnectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        return ConnectionFactoryInitializer().apply {
            setConnectionFactory(writeConnectionFactory)
            setDatabasePopulator(
                CompositeDatabasePopulator(
                    ResourceDatabasePopulator(ClassPathResource("schema.sql")),
                    ResourceDatabasePopulator(ClassPathResource("data.sql")),
                )
            )
        }
    }

    @Bean
    fun r2dbcTransactionManager(connectionFactory: ConnectionFactory): R2dbcTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }

    @Bean("writeTransactionalOperator")
    fun writeTransactionalOperator(transactionManager: R2dbcTransactionManager): TransactionalOperator {
        return TransactionalOperator.create(
            transactionManager,
            transactionDefinition(readOnly = false)
        )
    }

    @Bean("readTransactionalOperator")
    fun readTransactionalOperator(transactionManager: R2dbcTransactionManager): TransactionalOperator {
        return TransactionalOperator.create(
            transactionManager,
            transactionDefinition(readOnly = true)
        )
    }

    private fun transactionDefinition(readOnly: Boolean): TransactionDefinition {
        return DefaultTransactionDefinition().apply {
            isReadOnly = readOnly
        }
    }

    private fun createConnectionFactory(label: String, properties: R2dbcProperties): ConnectionFactory {
        val target = createTargetConnectionFactory(properties)
        if (!properties.pool.isEnabled) {
            return target
        }

        val pool = createPooledConnectionFactory(label, target, properties)
        warmupPool(label, pool, properties)
        return pool
    }

    private fun createTargetConnectionFactory(properties: R2dbcProperties): ConnectionFactory {
        val url = requireNotNull(properties.url) { "R2DBC url must be configured" }
        val builder = ConnectionFactoryBuilder.withUrl(url)

        properties.username
            ?.takeIf { it.isNotBlank() }
            ?.let(builder::username)

        properties.password
            ?.takeIf { it.isNotBlank() }
            ?.let(builder::password)

        builder.configure { options ->
            properties.properties.forEach { (key, value) ->
                options.option(Option.valueOf(key), value)
            }
        }

        return builder.build()
    }

    private fun createPooledConnectionFactory(
        label: String,
        target: ConnectionFactory,
        properties: R2dbcProperties,
    ): ConnectionPool {
        val pool = properties.pool
        val builder = ConnectionPoolConfiguration.builder(target)
            .name("${label}-pool")
            .initialSize(pool.initialSize)
            .minIdle(pool.minIdle)
            .maxSize(pool.maxSize)
            .maxIdleTime(pool.maxIdleTime)
            .acquireRetry(pool.acquireRetry)

        pool.maxLifeTime?.let(builder::maxLifeTime)
        pool.maxAcquireTime?.let(builder::maxAcquireTime)
        pool.maxCreateConnectionTime?.let(builder::maxCreateConnectionTime)
        pool.maxValidationTime?.let(builder::maxValidationTime)
        pool.validationQuery?.takeIf { it.isNotBlank() }?.let(builder::validationQuery)
        builder.validationDepth(pool.validationDepth)

        return ConnectionPool(builder.build())
    }

    private fun warmupPool(label: String, pool: ConnectionPool, properties: R2dbcProperties) {
        val targetConnections = maxOf(properties.pool.initialSize, properties.pool.minIdle)
        if (targetConnections <= 0) {
            return
        }

        log.info(
            "Warming up R2DBC pool '{}' with {} connections",
            "${label}-pool",
            targetConnections,
        )

        val warmed = pool.warmup().defaultIfEmpty(0).block() ?: 0

        log.info(
            "R2DBC pool '{}' warmup completed: added={}, allocated={}, idle={}",
            "${label}-pool",
            warmed,
            pool.metrics.map { it.allocatedSize() }.orElse(-1),
            pool.metrics.map { it.idleSize() }.orElse(-1),
        )
    }
}
