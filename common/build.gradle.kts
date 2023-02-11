import io.ajeet.plugin.Libraries

dependencies {
    implementation(Libraries.vertx_web)
    implementation(Libraries.vertx_opentracing)

    implementation(Libraries.vertx_lang_kotlin)
    implementation(Libraries.vertx_lang_kotlin_coroutines)

    implementation(Libraries.vertx_cassandra_client)
    implementation(Libraries.vertx_kafka_client)
}
