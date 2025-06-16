package org.test.kotlin_base.infrastructure.database.configuration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource



@Configuration
@EnableTransactionManagement
class DataSourceConfig {
    @Bean("writeHikariConfig")
    @ConfigurationProperties(prefix = "spring.datasource.write.hikari")
    fun writeHikariConfig(): HikariConfig {
        return HikariConfig()
    }

    @Bean("readHikariConfig")
    @ConfigurationProperties(prefix = "spring.datasource.read.hikari")
    fun readHikariConfig(): HikariConfig {
        return HikariConfig()
    }

    @Bean("writeDataSource")
    fun writableDataSource(@Qualifier("writeHikariConfig") writeHikariConfig: HikariConfig): DataSource {
        return HikariDataSource(writeHikariConfig)
    }

    @Bean("readDataSource")
    fun readonlyDataSource(@Qualifier("readHikariConfig") writeHikariConfig: HikariConfig): DataSource {
        return HikariDataSource(writeHikariConfig)
    }

    @Bean
    @Primary
    fun dataSource(
        @Qualifier("writeDataSource") writeDataSource: DataSource,
        @Qualifier("readDataSource") readDataSource: DataSource,
    ): DataSource {
        return LazyConnectionDataSourceProxy(writeDataSource).apply {
            setReadOnlyDataSource(readDataSource)
        }
    }
}
