package io.ajeet.poc.api

import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.tracing.opentracing.OpenTracingOptions
import io.ajeet.poc.common.tracing.createTracer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main() {
    val start = System.currentTimeMillis()
    val log: Logger = LoggerFactory.getLogger("Main")
    val tracer = createTracer()
    Vertx.vertx(VertxOptions().setTracingOptions(OpenTracingOptions(tracer)))
        .deployVerticle(MainVerticle(tracer))
        .onComplete { log.info("completed in {} milliseconds", System.currentTimeMillis() - start) }
        .onFailure { log.warn("failed in {} milliseconds with ${it.message}", System.currentTimeMillis() - start, it) }
}
