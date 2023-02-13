package io.ajeet.poc.common.repository.impl

import com.datastax.oss.driver.api.core.cql.Row
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import io.ajeet.poc.common.cassandra.UserTable
import io.ajeet.poc.common.cassandra.UserTokenTable
import io.ajeet.poc.common.repository.UserTokenRepository
import io.ajeet.poc.common.repository.model.UserTokenRecord
import io.vertx.cassandra.CassandraClient
import io.vertx.cassandra.ResultSet
import io.vertx.core.Future
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*

class CassandraUserTokenRepository(private val databaseClient: CassandraClient): UserTokenRepository {
    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(UserTokenRepository::class.java)

        private val SELECT_TOKEN_QUERY : String = """
            SELECT ${UserTokenTable.Fields.values().joinToString(separator = " ")}
             FROM ${UserTokenTable.name}
             WHERE ${UserTokenTable.Fields.userid} = :userid AND ${UserTokenTable.Fields.token} = :token
        """.trimIndent()

        private val INSERT_TOKEN_QUERY = """
            INSERT INTO user_login_tokens(userid, token, created_date, token_data)
             VALUES(:userid, :token, :created_date, :token_data)
        """.trimIndent()
    }

    override fun getToken(userId: UUID, refreshToken: UUID): Future<UserTokenRecord?> {
        val statement : SimpleStatement = SimpleStatement.builder(SELECT_TOKEN_QUERY)
            .addNamedValue("userid", userId)
            .addNamedValue("token", refreshToken)
            .build()

        return this.databaseClient.execute(statement)
            .onFailure { LOG.warn("error", it) }
            .transform{ mapRecord(it.result()) }
    }

    override fun saveToken(tokenRecord: UserTokenRecord): Future<Boolean> {
        val statement : SimpleStatement = SimpleStatement.builder(INSERT_TOKEN_QUERY)
            .addNamedValue("userid", tokenRecord.userid)
            .addNamedValue("token", tokenRecord.token)
            .addNamedValue("created_date", Instant.now())
            .addNamedValue("token_data", tokenRecord.tokenData)
            .build()

        return this.databaseClient.execute(statement)
            .onFailure { LOG.warn("failed", it)}
            .transform { Future.succeededFuture(it.succeeded()) }
    }

    private fun mapRecord(result: ResultSet) : Future<UserTokenRecord?> {
        return mapRecord(result.one())
    }

    private fun mapRecord(row: Row?) : Future<UserTokenRecord?> {
        return row?.let {
            val userRecord = UserTokenRecord(it.getUuid(UserTokenTable.Fields.userid.name))
            userRecord.token = it.getUuid(UserTokenTable.Fields.token.name)
            userRecord.tokenData = it.getString(UserTokenTable.Fields.token_data.name)
            userRecord.createdDate = it.getInstant(UserTable.Fields.created_date.name)
            Future.succeededFuture(userRecord)
        } ?: Future.succeededFuture()
    }
}