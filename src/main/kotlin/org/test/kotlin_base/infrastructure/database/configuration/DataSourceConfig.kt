package org.test.kotlin_base.infrastructure.database.configuration

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
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

@ConfigurationProperties(prefix = "app.r2dbc")
data class RoutingR2dbcProperties(
    var write: Connection = Connection(),
    var read: Connection = Connection(),
) {
    data class Connection(
        var url: String = "",
    )
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
                context.get<DatabaseRoute>(DatabaseRouteContext.KEY)
            } else {
                DatabaseRoute.WRITE
            }
            Mono.just(route)
        }
    }
}

@Configuration
@EnableR2dbcRepositories(basePackages = ["org.test.kotlin_base.domain"])
@EnableConfigurationProperties(RoutingR2dbcProperties::class)
class DataSourceConfig {
    @Bean("writeConnectionFactory")
    fun writeConnectionFactory(properties: RoutingR2dbcProperties): ConnectionFactory {
        return ConnectionFactories.get(properties.write.url)
    }

    @Bean("readConnectionFactory")
    fun readConnectionFactory(properties: RoutingR2dbcProperties): ConnectionFactory {
        return ConnectionFactories.get(properties.read.url)
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
}
