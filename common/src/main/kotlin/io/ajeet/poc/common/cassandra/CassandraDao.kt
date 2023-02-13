package io.ajeet.poc.common.cassandra

import io.ajeet.poc.common.config.CassandraConfigs
import io.vertx.cassandra.CassandraClient
import io.vertx.cassandra.CassandraClientOptions
import io.vertx.core.Vertx

class CassandraDao(vertx: Vertx, configs: CassandraConfigs) {
    private val client: CassandraClient

    init {
        val options = CassandraClientOptions().apply {
            configs.contactPoints.forEach { (host, port) -> addContactPoint(host, port) }
            keyspace = configs.keyspace
            password = configs.password
            username = configs.username
        }
        client = CassandraClient.createShared(vertx, "sharedClient", options)
    }

    fun getClient(): CassandraClient {
        return client
    }
}
