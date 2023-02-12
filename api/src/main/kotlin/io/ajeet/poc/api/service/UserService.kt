package io.ajeet.poc.api.service

import com.datastax.oss.driver.api.core.cql.SimpleStatement
import io.ajeet.poc.api.MainVerticle
import io.ajeet.poc.api.controller.UserDto
import io.ajeet.poc.common.cassandra.UserTable
import io.vertx.cassandra.CassandraClient
import java.util.*

class UserService(private val databaseClient: CassandraClient) {
    constructor(app: MainVerticle) : this(app.cassandraClient)

    suspend fun findUser(id: UUID) : UserDto? {
        val query = """
            SELECT ${UserTable.Fields.values().joinToString(separator = " ")}
             FROM ${UserTable.name}
             WHERE ${UserTable.Fields.userid} = :userid
        """.trimIndent()

        val statement : SimpleStatement = SimpleStatement.builder(query)
            .addNamedValue("userid", id)
            .build()

        this.databaseClient.execute(statement)

        return UserDto(id, "@todo")
    }
}
