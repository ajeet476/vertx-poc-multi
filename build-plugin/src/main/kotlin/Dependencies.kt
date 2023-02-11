package io.ajeet.plugin

object Versions {
    const val vertxVersion = "4.3.7"
    const val junitJupiterVersion = "5.7.0"
    const val orgSlf4j = "2.0.6"
    const val jacksonDatabind = "2.14.2"
}

object Libraries {
    const val vertx_stack_depchain = "io.vertx:vertx-stack-depchain:${Versions.vertxVersion}"

    const val vertx_web = "io.vertx:vertx-web"
    const val vertx_opentracing = "io.vertx:vertx-opentracing"

    const val vertx_cassandra_client = "io.vertx:vertx-cassandra-client"
    const val vertx_kafka_client = "io.vertx:vertx-kafka-client"

    const val vertx_lang_kotlin = "io.vertx:vertx-lang-kotlin"
    const val vertx_lang_kotlin_coroutines = "io.vertx:vertx-lang-kotlin-coroutines"

    const val slf4j_api = "org.slf4j:slf4j-api:${Versions.orgSlf4j}"
    const val slf4j_simple = "org.slf4j:slf4j-simple:${Versions.orgSlf4j}"
    const val jackson_databind = "com.fasterxml.jackson.core:jackson-databind:${Versions.jacksonDatabind}"

    const val stdlib_jdk8 = "stdlib-jdk8"
    const val vertx_junit5 = "io.vertx:vertx-junit5"
    const val junit_jupiter = "org.junit.jupiter:junit-jupiter:${Versions.junitJupiterVersion}"
}