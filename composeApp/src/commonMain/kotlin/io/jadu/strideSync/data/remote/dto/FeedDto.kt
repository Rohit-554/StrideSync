package io.jadu.strideSync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FeedItemResponse(
    val activity: ActivityResponse,
    val user: UserResponse,
    val kudosCount: Int,
    val commentCount: Int,
    val hasKudosed: Boolean,
)

@Serializable
data class CommentResponse(
    val id: String,
    val userId: String,
    val displayName: String,
    val text: String,
    val createdAt: Long,
)

@Serializable
data class UserProfileResponse(
    val id: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val activityCount: Int,
    val followerCount: Int,
    val followingCount: Int,
)

@Serializable
data class AthleteSummaryResponse(
    val id: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val followerCount: Int,
    val activityCount: Int,
    val isFollowing: Boolean,
)

@Serializable
data class StatusResponse(
    val id: String,
    val userId: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val text: String,
    val backgroundHex: String,
    val createdAt: Long,
    val expiresAt: Long,
    val isOwn: Boolean,
)

@Serializable
data class AddCommentRequest(
    val text: String,
)

@Serializable
data class CreateStatusRequest(
    val text: String,
    val backgroundHex: String,
)
