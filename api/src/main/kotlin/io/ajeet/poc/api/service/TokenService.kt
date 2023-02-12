package io.ajeet.poc.api.service

import com.datastax.oss.driver.api.core.cql.SimpleStatement
import io.ajeet.poc.api.MainVerticle
import io.ajeet.poc.api.controller.Token
import io.ajeet.poc.api.controller.TokenDto
import io.ajeet.poc.api.controller.UserDto
import io.ajeet.poc.common.kafka.MessagePublisher
import io.vertx.cassandra.CassandraClient
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*
import kotlin.random.Random

class TokenService(private val kafkaPublisher: MessagePublisher, private val databaseClient: CassandraClient) {
    constructor(app: MainVerticle) : this(
        kafkaPublisher = app.kafkaPublisher,
        databaseClient = app.cassandraClient
    )

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(TokenService::class.java)
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
        val refreshTokenValue = JsonObject.of()
            .put("name", user.name)
            .put("loggedInAt", Instant.now())
            .toString()

        val query = """
            INSERT INTO user_login_tokens(userid, token, added_date, token_data)
             VALUES(:userid, :token, :added_date, :token_data)
        """.trimIndent()

        val statement : SimpleStatement = SimpleStatement.builder(query)
            .addNamedValue("userid", user.id)
            .addNamedValue("token", token)
            .addNamedValue("added_date", Instant.now())
            .addNamedValue("token_data", refreshTokenValue)
            .build()
        this.databaseClient.execute(statement)
            .onSuccess { LOG.info("response {}", it) }
            .onFailure { LOG.warn("failed", it)}
            .await()

        return token
    }
}
