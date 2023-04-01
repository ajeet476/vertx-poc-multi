package io.ajeet.poc.api.controller

import io.ajeet.poc.api.MainVerticle
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Vertx
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
class TokenControllerTest {
    companion object {
        private val LOG: Logger = LoggerFactory.getLogger("TokenControllerTest")

        @BeforeAll
        @JvmStatic
        fun setup(vertx: Vertx, context: VertxTestContext) {
            vertx.deployVerticle(MainVerticle(), context.succeeding {
                LOG.info("deployed vertical")
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
            })
        }
    }

    object TestServer {
        const val PORT = 8888
        const val HOST = "localhost"
    }

    @Test
    @Timeout(2)
    fun generateToken(vertx: Vertx, context: VertxTestContext) {
        val client = vertx.createHttpClient()
        client.request(HttpMethod.GET, TestServer.PORT, TestServer.HOST, "/health")
            .compose{ req -> req.send().compose(HttpClientResponse::body)}
            .onSuccess {
                context.verify {
                    assertEquals("{\"status\":\"up\"}", it.toString(), "body matches")
                }
            }.onFailure {
                context.failNow(it)
            }
    }
}