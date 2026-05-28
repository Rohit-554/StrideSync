package io.jadu.strideSync.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

object DatabaseConfig {

    fun createDataSource(): HikariDataSource {
        val url = EnvConfig.require("DB_URL")
        val user = EnvConfig.require("DB_USER")
        val password = EnvConfig.require("DB_PASSWORD")

        val config = HikariConfig().apply {
            jdbcUrl = url
            username = user
            this.password = password
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
            minimumIdle = 2
            idleTimeout = 600_000
            connectionTimeout = 30_000
            maxLifetime = 1_800_000
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        return HikariDataSource(config)
    }
}
