package io.ajeet.poc.api.integration

import io.vertx.core.Vertx
import io.vertx.core.http.HttpClientResponse
import io.vertx.core.http.HttpMethod
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(VertxExtension::class)
class TokenApiTest: AbstractIntegrationTest() {

    @Test
    @Timeout(2)
    fun generateToken(vertx: Vertx, context: VertxTestContext) {
        val client = vertx.createHttpClient(options)
        client.request(HttpMethod.POST, "/token")
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