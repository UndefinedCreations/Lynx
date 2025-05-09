package com.undefined.lynx.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.sql.ResultSet

class MySQL(
    ip: String,
    port: Int,
    database: String,
    username: String,
    password: String,
    maxPoolSize: Int = 10,
    poolName: String,
) : LynxSQL {

    val config = HikariConfig().apply {
        this.jdbcUrl = "jdbc:mysql://$ip:$port/$database"
        this.username = username
        this.password = password
        this.maximumPoolSize = maxPoolSize
        this.poolName = poolName
    }
    val dataSource = HikariDataSource(config)

    override fun <T> executeQuery(sql: String, params: List<String>, resetSet: ResultSet.() -> T): Flux<T> =
        Flux.create { emitter ->
            dataSource.connection.use { connection ->
                connection.prepareStatement(sql).use { statement ->
                    for ((i, param) in params.withIndex())
                        statement.setString(i + 1, param)
                    statement.executeQuery().use { rs ->
                        while (rs.next()) emitter.next(resetSet(rs))
                    }
                }
            }
        }.onErrorResume {
            throw it
        }.subscribeOn(Schedulers.boundedElastic())

    override fun executeQuery(sql: String, params: List<String>): Flux<ResultSet> =
        Flux.create { emitter ->
            dataSource.connection.use { connection ->
                connection.prepareStatement(sql).use { statement ->
                    for ((i, param) in params.withIndex())
                        statement.setString(i + 1, param)
                    statement.executeQuery().use { rs ->
                        while (rs.next()) emitter.next(rs)
                    }
                }
            }
        }.onErrorResume {
            throw it
        }.subscribeOn(Schedulers.boundedElastic())

    override fun execute(sql: String, params: List<String>): Mono<Boolean> =
        Mono.create<Boolean?> {
            dataSource.connection.use { connection ->
                connection.prepareStatement(sql).use { statement ->
                    for ((i, param) in params.withIndex())
                        statement.setString(i + 1, param)
                    statement.execute()
                }
            }
        }.onErrorResume {
            throw it
        }.subscribeOn(Schedulers.boundedElastic())
}