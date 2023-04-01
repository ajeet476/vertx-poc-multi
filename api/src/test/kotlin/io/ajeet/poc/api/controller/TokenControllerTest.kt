package io.ajeet.poc.api.controller

import io.ajeet.poc.api.service.TokenService
import io.mockk.*
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

@OptIn(ExperimentalCoroutinesApi::class)
class TokenControllerTest {
    private val service: TokenService = mockk()
    private val controller = TokenController(service)

    @Test
    fun generateToken() = runTest {
        val ctx: RoutingContext = mockk()

        coEvery { service.generateToken(any()) } returns TokenDto(
            refreshToken = Token(token ="2", expiresInSeconds = 12),
            accessToken = Token(token ="23445", expiresInSeconds = 1)
        )

        verify (exactly = 1) {
            ctx.end(Buffer.buffer("""{"refreshToken":{"token":"2","expiresInSeconds":12},"accessToken":{"token":"23445","expiresInSeconds":1}"""))
        }

        controller.generateToken(ctx)
    }
}