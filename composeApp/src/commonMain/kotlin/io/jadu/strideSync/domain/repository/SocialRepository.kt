package io.jadu.strideSync.domain.repository

import io.jadu.strideSync.domain.model.Comment
import io.jadu.strideSync.domain.model.AthleteSummary
import io.jadu.strideSync.domain.model.StatusItem
import io.jadu.strideSync.domain.model.UserProfile

interface SocialRepository {
    suspend fun followUser(userId: String): Result<Unit>

    suspend fun unfollowUser(userId: String): Result<Unit>

    suspend fun toggleKudos(activityId: String, currentlyKudosed: Boolean): Result<Boolean>

    suspend fun addComment(
        activityId: String,
        text: String
    ): Result<Comment>

    suspend fun getComments(activityId: String): Result<List<Comment>>

    suspend fun getUserProfile(userId: String): Result<UserProfile>

    suspend fun searchAthletes(query: String, page: Int, size: Int): Result<List<AthleteSummary>>

    suspend fun getSuggestedAthletes(limit: Int): Result<List<AthleteSummary>>

    suspend fun getStatuses(): Result<List<StatusItem>>

    suspend fun createStatus(text: String, backgroundHex: String): Result<StatusItem>
}
