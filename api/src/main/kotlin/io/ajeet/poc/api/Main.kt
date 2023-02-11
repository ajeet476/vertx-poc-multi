package io.ajeet.poc.api

import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.tracing.opentracing.OpenTracingOptions
import io.ajeet.poc.common.tracing.createTracer

fun main() {
    val tracer = createTracer()
    Vertx.vertx(VertxOptions().setTracingOptions(OpenTracingOptions(tracer)))
        .deployVerticle(MainVerticle(tracer))
        .onComplete { println("completed") }
        .onFailure { println("failed ${it.message} ${it.stackTrace}") }
}