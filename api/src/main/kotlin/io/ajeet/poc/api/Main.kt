package io.ajeet.poc.api

import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.tracing.opentracing.OpenTracingOptions
import io.jaegertracing.Configuration
import io.opentracing.util.GlobalTracer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main() {
    val start = System.currentTimeMillis()
    val log: Logger = LoggerFactory.getLogger("Main")
    val tracer = Configuration.fromEnv().tracer
    GlobalTracer.registerIfAbsent(tracer)
    Vertx.vertx(VertxOptions().setTracingOptions(OpenTracingOptions(tracer)))
        .deployVerticle(MainVerticle())
        .onComplete { log.info("completed in {} milliseconds", System.currentTimeMillis() - start) }
        .onFailure { log.warn("failed in {} milliseconds with ${it.message}", System.currentTimeMillis() - start, it) }
}
