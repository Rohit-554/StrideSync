package io.jadu.strideSync.domain.model

data class User(
    val id: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String? = null
)
