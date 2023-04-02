package io.ajeet.poc.api.integration

import io.ajeet.poc.api.MainVerticle
import io.jaegertracing.Configuration
import io.opentracing.util.GlobalTracer
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ExtendWith(VertxExtension::class)
abstract class AbstractIntegrationTest {
    companion object {
        private val LOG: Logger = LoggerFactory.getLogger("TokenApiTest")
        lateinit var vertical: MainVerticle

        @BeforeAll
        @JvmStatic
        fun setup(vertx: Vertx, context: VertxTestContext) {
            vertical = MainVerticle()
            val tracer = Configuration.fromEnv().tracer
            GlobalTracer.registerIfAbsent(tracer)
            vertx.deployVerticle(vertical, context.succeeding {
                LOG.info("deployed vertical")
                context.completeNow()
            })
        }

        @AfterAll
        @JvmStatic
        fun cleanup(vertx: Vertx, context: VertxTestContext) {
            LOG.info("removing vertical...")
            val futures: List<Future<Void>> = vertx.deploymentIDs().map { id ->
                vertx.undeploy(id)
            }
            CompositeFuture.join(futures).andThen(context.succeeding {
                LOG.info("removed vertical.")
                context.completeNow()
            })
        }
    }
}