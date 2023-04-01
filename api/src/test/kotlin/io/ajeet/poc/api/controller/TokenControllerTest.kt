package io.ajeet.poc.api.controller

import io.ajeet.poc.api.MainVerticle
import io.vertx.core.CompositeFuture
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClientResponse
import io.vertx.core.http.HttpMethod
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(VertxExtension::class)
class TokenControllerTest {
    companion object {
        @BeforeAll
        @JvmStatic
        fun setup(vertx: Vertx, context: VertxTestContext) {
            vertx.deployVerticle(MainVerticle(), context.succeeding {
                println("deployed vertical")
            })
        }

        @AfterAll
        @JvmStatic
        fun cleanup(vertx: Vertx, context: VertxTestContext) {
            val futures = vertx.deploymentIDs().map { id ->
                vertx.undeploy(id)
            }
            val join = CompositeFuture.join(futures)
            println("removed vertical" + join.isComplete)
            context.completeNow()
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