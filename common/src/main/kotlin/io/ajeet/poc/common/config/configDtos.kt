package io.ajeet.poc.common.config

class CassandraConfigs {
    var contactPoints: Map<String, Int> = mapOf()
    var keyspace: String = "my_keyspace"
    var username: String = "cassandra"
    var password: String = "cassandra"
}

class KafkaConfigs {
    var bootstrapServers: String = "localhost:9092"
    var groupId: String = "my_group"
    var autoOffsetReset: String = "earliest"
    var enableAutoCommit: String = "false"
    var topics: Map<String, String> = mapOf()
}
