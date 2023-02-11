import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.ajeet.plugin.Libraries

plugins {
    application
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":common"))
    implementation(Libraries.vertx_web)
    implementation(Libraries.vertx_cassandra_client)
    implementation(Libraries.vertx_kafka_client)
    implementation(Libraries.vertx_opentracing)
    implementation(Libraries.vertx_lang_kotlin)
    implementation(Libraries.vertx_lang_kotlin_coroutines)

    implementation(Libraries.jackson_databind)

    testImplementation(Libraries.vertx_junit5)
    testImplementation(Libraries.junit_jupiter)
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("fat")
    mergeServiceFiles()
}
