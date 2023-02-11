import io.ajeet.plugin.Libraries
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    id("com.github.johnrengelman.shadow") version "7.0.0" apply false
    id("com.avast.gradle.docker-compose") version "0.16.11" apply false
}

buildscript {
    dependencies {
        classpath("io.ajeet:build-plugin")
    }
}

allprojects {
    group = "io.ajeet"
    version = "1.0-SNAPSHOT"
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(platform(Libraries.vertx_stack_depchain))
        implementation(kotlin(Libraries.stdlib_jdk8))
        implementation(Libraries.slf4j_api)
        implementation(Libraries.slf4j_simple)

        testImplementation(kotlin("test"))
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events = setOf(PASSED, SKIPPED, FAILED)
        }
    }
}
