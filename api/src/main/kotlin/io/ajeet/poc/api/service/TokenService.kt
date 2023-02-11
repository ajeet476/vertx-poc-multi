package io.ajeet.poc.api.service

import io.ajeet.poc.api.controller.Token
import io.ajeet.poc.api.controller.TokenDto
import io.ajeet.poc.api.controller.UserDto
import io.ajeet.poc.common.kafka.KafkaPublisher
import io.vertx.core.json.JsonObject
import java.time.Instant
import kotlin.random.Random

class TokenService(private val kafkaPublisher: KafkaPublisher) {
    fun generateToken(user: UserDto) : TokenDto {
        val refreshTokenValue = JsonObject.of()
            .put("id", user.id)
            .put("name", user.name)
            .put("loggedInAt", Instant.now())
            .put("token", Random.nextBytes(90).toString())
            .toString()

        val accessToken = Token(token = Random.nextBytes(40).toString(), expiresInSeconds = 60)
        val refreshToken = Token(token = refreshTokenValue, expiresInSeconds = 3600)
        val token = TokenDto(accessToken = accessToken, refreshToken = refreshToken)

        this.kafkaPublisher.publish("poc-topic", "...sending...$token for $user")

        return token
    }
}
