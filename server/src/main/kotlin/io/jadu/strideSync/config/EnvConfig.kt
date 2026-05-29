package io.jadu.strideSync.config

import java.io.File

object EnvConfig {
    private val fileValues: Map<String, String> by lazy {
        findEnvFile()
            ?.readLines()
            ?.asSequence()
            ?.map(String::trim)
            ?.filter { it.isNotEmpty() && !it.startsWith("#") && "=" in it }
            ?.associate { line ->
                val (key, value) = line.split("=", limit = 2)
                key.trim() to value.trim()
            }
            .orEmpty()
    }

    fun get(name: String): String? = System.getenv(name) ?: fileValues[name]

    fun require(name: String): String =
        get(name) ?: error("Required environment variable '$name' is not set")

    private fun findEnvFile(): File? =
        generateSequence(File(System.getProperty("user.dir")).absoluteFile) { it.parentFile }
            .map { it.resolve(".env") }
            .firstOrNull(File::isFile)
}
