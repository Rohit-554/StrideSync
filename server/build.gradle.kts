plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
    application
}

group = "io.jadu.strideSync"
version = "1.0.0"
application {
    mainClass.set("io.jadu.strideSync.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

// Load .env from project root and forward all vars to the run task
val envFile = rootProject.file(".env")
if (envFile.exists()) {
    val envVars = envFile.readLines()
        .filter { it.isNotBlank() && !it.startsWith("#") && "=" in it }
        .associate { line ->
            val (key, value) = line.split("=", limit = 2)
            key.trim() to value.trim()
        }
    tasks.named<JavaExec>("run") {
        environment(envVars)
    }
    tasks.named<Test>("test") {
        environment(envVars)
    }
}

dependencies {
    // Ktor server
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.serverContentNegotiation)
    implementation(libs.ktor.server.serialization.json)
    implementation(libs.ktor.serverAuth)
    implementation(libs.ktor.serverAuthJwt)
    implementation(libs.ktor.serverWebsockets)
    implementation(libs.ktor.serverCors)
    implementation(libs.ktor.serverCallLogging)

    // Exposed ORM
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)

    // Database
    implementation(libs.postgresql.jdbc)
    implementation(libs.hikaricp)

    // Security
    implementation(libs.bcrypt)

    // Logging
    implementation(libs.logback)

    // Test
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}