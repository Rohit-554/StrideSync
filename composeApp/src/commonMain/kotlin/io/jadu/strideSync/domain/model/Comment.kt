package io.jadu.strideSync.domain.model

data class Comment(
    val id: String,
    val userId: String,
    val displayName: String,
    val text: String,
    val createdAt: Long
)
