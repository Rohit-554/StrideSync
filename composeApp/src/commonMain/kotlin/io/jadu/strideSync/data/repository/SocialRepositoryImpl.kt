package io.jadu.strideSync.data.repository

import io.jadu.strideSync.data.local.dao.FeedDao
import io.jadu.strideSync.data.remote.api.SocialApi
import io.jadu.strideSync.data.remote.dto.AthleteSummaryResponse
import io.jadu.strideSync.data.remote.dto.CommentResponse
import io.jadu.strideSync.data.remote.dto.UserProfileResponse
import io.jadu.strideSync.domain.model.AthleteSummary
import io.jadu.strideSync.domain.model.Comment
import io.jadu.strideSync.domain.model.StatusItem
import io.jadu.strideSync.domain.model.User
import io.jadu.strideSync.domain.model.UserProfile
import io.jadu.strideSync.domain.repository.SocialRepository

class SocialRepositoryImpl(
    private val socialApi: SocialApi,
    private val feedDao: FeedDao
) : SocialRepository {

    override suspend fun followUser(userId: String): Result<Unit> = runCatching {
        socialApi.follow(userId)
        feedDao.clearAll()
    }

    override suspend fun unfollowUser(userId: String): Result<Unit> = runCatching {
        socialApi.unfollow(userId)
        feedDao.clearAll()
    }

    override suspend fun toggleKudos(activityId: String, currentlyKudosed: Boolean): Result<Boolean> = runCatching {
        val hasKudosed = if (currentlyKudosed) {
            socialApi.removeKudos(activityId)
            false
        } else {
            socialApi.kudos(activityId)
            true
        }
        feedDao.clearAll()
        hasKudosed
    }

    override suspend fun addComment(
        activityId: String,
        text: String
    ): Result<Comment> = runCatching {
        val response = socialApi.addComment(activityId, text)
        feedDao.clearAll()
        response.toDomain()
    }

    override suspend fun getComments(activityId: String): Result<List<Comment>> = runCatching {
        socialApi.getComments(activityId).map { it.toDomain() }
    }

    override suspend fun getUserProfile(userId: String): Result<UserProfile> = runCatching {
        socialApi.getUser(userId).toDomain()
    }

    override suspend fun searchAthletes(query: String, page: Int, size: Int): Result<List<AthleteSummary>> = runCatching {
        socialApi.searchAthletes(query = query, page = page, size = size).map { it.toDomain() }
    }

    override suspend fun getSuggestedAthletes(limit: Int): Result<List<AthleteSummary>> = runCatching {
        socialApi.getSuggestedAthletes(limit).map { it.toDomain() }
    }

    override suspend fun getStatuses(): Result<List<StatusItem>> = runCatching {
        socialApi.getStatuses().map { it.toDomain() }
    }

    override suspend fun createStatus(text: String, backgroundHex: String): Result<StatusItem> = runCatching {
        socialApi.createStatus(text = text, backgroundHex = backgroundHex).toDomain()
    }
}

internal fun CommentResponse.toDomain(): Comment = Comment(
    id = id,
    userId = userId,
    displayName = displayName,
    text = text,
    createdAt = createdAt
)

internal fun UserProfileResponse.toDomain(): UserProfile = UserProfile(
    user = User(
        id = id,
        displayName = displayName,
        email = "",
        avatarUrl = avatarUrl
    ),
    activityCount = activityCount,
    followerCount = followerCount,
    followingCount = followingCount
)

internal fun AthleteSummaryResponse.toDomain(): AthleteSummary = AthleteSummary(
    id = id,
    displayName = displayName,
    avatarUrl = avatarUrl,
    followerCount = followerCount,
    activityCount = activityCount,
    isFollowing = isFollowing
)

internal fun io.jadu.strideSync.data.remote.dto.StatusResponse.toDomain(): StatusItem = StatusItem(
    id = id,
    userId = userId,
    displayName = displayName,
    avatarUrl = avatarUrl,
    text = text,
    backgroundHex = backgroundHex,
    createdAt = createdAt,
    expiresAt = expiresAt,
    isOwn = isOwn
)
