package io.jadu.strideSync.data.remote.api

import io.jadu.strideSync.data.remote.dto.AddCommentRequest
import io.jadu.strideSync.data.remote.dto.AthleteSummaryResponse
import io.jadu.strideSync.data.remote.dto.CommentResponse
import io.jadu.strideSync.data.remote.dto.CreateStatusRequest
import io.jadu.strideSync.data.remote.dto.StatusResponse
import io.jadu.strideSync.data.remote.dto.UserProfileResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val PATH_USERS = "/users"
private const val PATH_FOLLOW = "/follow"
private const val PATH_KUDOS = "/kudos"
private const val PATH_COMMENTS = "/comments"
private const val PATH_STATUSES = "/statuses"

class SocialApi(private val client: HttpClient) {

    suspend fun follow(userId: String) {
        client.post("$PATH_USERS/$userId$PATH_FOLLOW")
    }

    suspend fun unfollow(userId: String) {
        client.delete("$PATH_USERS/$userId$PATH_FOLLOW")
    }

    suspend fun kudos(activityId: String) {
        client.post("/activities/$activityId$PATH_KUDOS")
    }

    suspend fun removeKudos(activityId: String) {
        client.delete("/activities/$activityId$PATH_KUDOS")
    }

    suspend fun addComment(activityId: String, text: String): CommentResponse =
        client.post("/activities/$activityId$PATH_COMMENTS") {
            contentType(ContentType.Application.Json)
            setBody(AddCommentRequest(text = text))
        }.body()

    suspend fun getComments(activityId: String): List<CommentResponse> =
        client.get("/activities/$activityId$PATH_COMMENTS").body()

    suspend fun getUser(userId: String): UserProfileResponse =
        client.get("$PATH_USERS/$userId").body()

    suspend fun searchAthletes(query: String, page: Int, size: Int): List<AthleteSummaryResponse> =
        client.get("$PATH_USERS/search") {
            parameter("q", query)
            parameter("page", page)
            parameter("size", size)
        }.body()

    suspend fun getSuggestedAthletes(limit: Int): List<AthleteSummaryResponse> =
        client.get("$PATH_USERS/suggestions") {
            parameter("limit", limit)
        }.body()

    suspend fun getStatuses(): List<StatusResponse> =
        client.get(PATH_STATUSES).body()

    suspend fun createStatus(text: String, backgroundHex: String): StatusResponse =
        client.post(PATH_STATUSES) {
            contentType(ContentType.Application.Json)
            setBody(CreateStatusRequest(text = text, backgroundHex = backgroundHex))
        }.body()
}
