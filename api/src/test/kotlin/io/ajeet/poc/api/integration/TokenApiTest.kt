package io.ajeet.poc.api.integration

import io.ajeet.poc.api.MainVerticle
import io.jaegertracing.Configuration
import io.opentracing.util.GlobalTracer
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.HttpClientResponse
import io.vertx.core.http.HttpMethod
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@ExtendWith(VertxExtension::class)
class TokenApiTest {
    companion object {
        private val LOG: Logger = LoggerFactory.getLogger("TokenApiTest")

        @BeforeAll
        @JvmStatic
        fun setup(vertx: Vertx, context: VertxTestContext) {
            val tracer = Configuration.fromEnv().tracer
            GlobalTracer.registerIfAbsent(tracer)
            vertx.deployVerticle(MainVerticle(), context.succeeding {
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

    @Test
    @Timeout(2)
    fun generateToken(vertx: Vertx, context: VertxTestContext) {
        val client = vertx.createHttpClient()
        client.request(HttpMethod.GET, TestServer.PORT, TestServer.HOST, "/health")
            .compose {
                    req -> req.send().compose(HttpClientResponse::body)
            }
            .onComplete(context.succeeding {
                context.verify {
                    assertEquals("{\"status\":\"up\"}", it.toString(), "body matches")
                    context.completeNow()
                }
            }).onFailure {
                context.failNow(it)
            }
    }
}