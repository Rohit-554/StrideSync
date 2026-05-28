package io.jadu.strideSync

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
