package io.jadu.strideSync.domain.model

data class StatusItem(
    val id: String,
    val userId: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val text: String,
    val backgroundHex: String,
    val createdAt: Long,
    val expiresAt: Long,
    val isOwn: Boolean
)
