package io.ajeet.poc.api.service

import io.ajeet.poc.api.controller.UserDto
import io.ajeet.poc.api.service.TokenService
import io.ajeet.poc.common.kafka.MessagePublisher
import io.ajeet.poc.common.repository.UserTokenRepository
import io.mockk.*
import io.vertx.core.Future
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class TokenServiceTest {
    private val kafkaPublisher: MessagePublisher = mockk(relaxed = true)
    private val tokenRepository: UserTokenRepository = mockk()
    private val service = TokenService(kafkaPublisher, tokenRepository)

    @Test
    fun generateToken() = runTest {
        coEvery { tokenRepository.saveToken(any()) } returns Future.succeededFuture()

        val dto = UserDto(
            id = UUID.randomUUID(),
            name = "a"
        )
        service.generateToken(dto)

        verify (exactly = 1) {
            kafkaPublisher.publish(any(), any())
        }
    }
}
