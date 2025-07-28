package com.undefined.lynx.sql

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.sql.ResultSet

interface LynxSQL {
    fun <T> executeQuery(sql: String, params: List<String> = listOf(), resetSet: ResultSet.() -> T): Flux<T>
    fun executeQuery(sql: String, params: List<String> = listOf()): Flux<ResultSet>
    fun execute(sql: String, params: List<String> = listOf()): Mono<Boolean>
}