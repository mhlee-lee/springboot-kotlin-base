package org.test.kotlin_base.adapter.output.persistence.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.jdbc.autoconfigure.ApplicationDataSourceScriptDatabaseInitializer
import org.springframework.boot.sql.autoconfigure.init.ApplicationScriptDatabaseInitializer
import org.springframework.boot.sql.autoconfigure.init.SqlInitializationProperties
import org.springframework.context.ResourceLoaderAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.io.ResourceLoader
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.sql.Connection
import java.util.concurrent.Executors
import javax.sql.DataSource

@Configuration
@EnableConfigurationProperties(SqlInitializationProperties::class)
class DatabasePersistenceConfiguration {

    @Bean("writeHikariConfig")
    @ConfigurationProperties(prefix = "spring.datasource.write.hikari")
    fun writeHikariConfig(): HikariConfig = HikariConfig()

    @Bean("readHikariConfig")
    @ConfigurationProperties(prefix = "spring.datasource.read.hikari")
    fun readHikariConfig(): HikariConfig = HikariConfig()

    @Bean("writeDataSource")
    fun writeDataSource(
        @Qualifier("writeHikariConfig")
        writeHikariConfig: HikariConfig,
    ): DataSource {
        return HikariDataSource(writeHikariConfig)
    }

    @Bean("readDataSource")
    fun readDataSource(
        @Qualifier("readHikariConfig")
        readHikariConfig: HikariConfig,
    ): DataSource {
        return HikariDataSource(readHikariConfig)
    }

    @Bean("dataSource")
    @Primary
    fun dataSource(
        @Qualifier("writeDataSource")
        writeDataSource: DataSource,
        @Qualifier("readDataSource")
        readDataSource: DataSource,
    ): DataSource {
        return LazyConnectionDataSourceProxy(writeDataSource).apply {
            setReadOnlyDataSource(readDataSource)
            setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED)
        }
    }

    @Bean("databaseCoroutineDispatcher", destroyMethod = "close")
    fun databaseCoroutineDispatcher(): ExecutorCoroutineDispatcher {
        return Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
    }

    @Bean("writeTransactionTemplate")
    fun writeTransactionTemplate(
        transactionManager: PlatformTransactionManager,
    ): TransactionTemplate {
        return TransactionTemplate(transactionManager).apply {
            isReadOnly = false
        }
    }

    @Bean("readTransactionTemplate")
    fun readTransactionTemplate(
        transactionManager: PlatformTransactionManager,
    ): TransactionTemplate {
        return TransactionTemplate(transactionManager).apply {
            isReadOnly = true
        }
    }

    @Bean
    fun applicationScriptDatabaseInitializer(
        @Qualifier("writeDataSource")
        writeDataSource: DataSource,
        @Qualifier("readDataSource")
        readDataSource: DataSource,
        properties: SqlInitializationProperties,
    ): ApplicationScriptDatabaseInitializer {
        return DualDataSourceScriptDatabaseInitializer(
            writeDataSource = writeDataSource,
            readDataSource = readDataSource,
            properties = properties,
        )
    }

    private class DualDataSourceScriptDatabaseInitializer(
        private val writeDataSource: DataSource,
        private val readDataSource: DataSource,
        private val properties: SqlInitializationProperties,
    ) : ApplicationScriptDatabaseInitializer, ResourceLoaderAware, org.springframework.beans.factory.InitializingBean {
        private var resourceLoader: ResourceLoader? = null

        override fun setResourceLoader(resourceLoader: ResourceLoader) {
            this.resourceLoader = resourceLoader
        }

        override fun afterPropertiesSet() {
            initialize(writeDataSource)
            initialize(readDataSource)
        }

        private fun initialize(dataSource: DataSource) {
            ApplicationDataSourceScriptDatabaseInitializer(dataSource, properties)
                .also { initializer ->
                    resourceLoader?.let(initializer::setResourceLoader)
                }
                .initializeDatabase()
        }
    }
}
