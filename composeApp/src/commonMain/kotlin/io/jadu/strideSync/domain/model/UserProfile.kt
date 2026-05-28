package io.jadu.strideSync.domain.model

data class UserProfile(
    val user: User,
    val activityCount: Int,
    val followerCount: Int,
    val followingCount: Int
)
