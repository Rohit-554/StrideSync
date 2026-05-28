package io.jadu.strideSync.domain.model

data class AthleteSummary(
    val id: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val followerCount: Int,
    val activityCount: Int,
    val isFollowing: Boolean
)
