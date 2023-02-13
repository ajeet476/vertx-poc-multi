package io.ajeet.poc.common.repository.impl

import com.datastax.oss.driver.api.core.cql.Row
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import io.ajeet.poc.common.cassandra.UserTable
import io.ajeet.poc.common.repository.UserRepository
import io.ajeet.poc.common.repository.model.UserRecord
import io.vertx.cassandra.CassandraClient
import io.vertx.cassandra.ResultSet
import io.vertx.core.Future
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class CassandraUserRepository(private val databaseClient: CassandraClient): UserRepository {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(UserRepository::class.java)

        private val SELECT_USER_QUERY : String = """
            SELECT ${UserTable.Fields.values().joinToString(separator = " ")}
             FROM ${UserTable.name}
             WHERE ${UserTable.Fields.userid} = :userid
        """.trimIndent()
    }

    override fun getUser(id: UUID): Future<UserRecord?> {
        val statement : SimpleStatement = SimpleStatement.builder(SELECT_USER_QUERY)
            .addNamedValue("userid", id)
            .build()

        return this.databaseClient.execute(statement)
            .onFailure { LOG.warn("error", it) }
            .transform{ mapRecord(it.result()) }
    }

    private fun mapRecord(result: ResultSet) : Future<UserRecord?> {
        return mapRecord(result.one())
    }

    private fun mapRecord(row: Row?) : Future<UserRecord?> {
        return row?.let {
            val userRecord = UserRecord(it.getUuid(UserTable.Fields.userid.name))
            userRecord.email = it.getString(UserTable.Fields.email.name)
            userRecord.firstname = it.getString(UserTable.Fields.firstname.name)
            userRecord.lastname = it.getString(UserTable.Fields.lastname.name)
            userRecord.createdDate = it.getInstant(UserTable.Fields.created_date.name)
            Future.succeededFuture(userRecord)
        } ?: Future.succeededFuture()
    }
}