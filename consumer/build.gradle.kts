import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(io.ajeet.plugin.Libraries.vertx_web)
    implementation(io.ajeet.plugin.Libraries.vertx_cassandra_client)
    implementation(io.ajeet.plugin.Libraries.vertx_kafka_client)
    implementation(io.ajeet.plugin.Libraries.vertx_opentracing)
    implementation(io.ajeet.plugin.Libraries.vertx_lang_kotlin)
    implementation(io.ajeet.plugin.Libraries.vertx_lang_kotlin_coroutines)

    implementation(io.ajeet.plugin.Libraries.jackson_databind)

    testImplementation(io.ajeet.plugin.Libraries.vertx_junit5)
    testImplementation(io.ajeet.plugin.Libraries.junit_jupiter)
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("fat")
    mergeServiceFiles()
}
