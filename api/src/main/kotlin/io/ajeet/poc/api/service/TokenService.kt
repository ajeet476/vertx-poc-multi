package io.ajeet.poc.api.service

import io.ajeet.poc.api.MainVerticle
import io.ajeet.poc.api.controller.Token
import io.ajeet.poc.api.controller.TokenDto
import io.ajeet.poc.api.controller.UserDto
import io.ajeet.poc.common.kafka.MessagePublisher
import io.ajeet.poc.common.repository.UserTokenRepository
import io.ajeet.poc.common.repository.model.UserTokenRecord
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*
import kotlin.random.Random

class TokenService(private val kafkaPublisher: MessagePublisher, private val tokenRepository: UserTokenRepository) {
    constructor(app: MainVerticle) : this(
        kafkaPublisher = app.kafkaPublisher,
        tokenRepository = app.userTokenRepository
    )

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(TokenService::class.java)
    }

    suspend fun generateToken(user: UserDto) : TokenDto {
        val refreshTokenValue = saveToken(user).toString()
        val accessToken = Token(token = Random.nextBytes(40).toString(), expiresInSeconds = 60)
        val refreshToken = Token(token = refreshTokenValue, expiresInSeconds = 3600)
        val token = TokenDto(accessToken = accessToken, refreshToken = refreshToken)

        this.kafkaPublisher.publish("poc-topic", "...sending...$token for $user")

        return token
    }

    private suspend fun saveToken(user: UserDto) : UUID {
        val token = UUID.randomUUID()

        val tokenRecord = UserTokenRecord(user.id)
        tokenRecord.token = token
        tokenRecord.tokenData = JsonObject.of()
            .put("name", user.name)
            .put("loggedInAt", Instant.now())
            .toString()

        this.tokenRepository.saveToken(tokenRecord)
            .await()

        return token
    }
}
