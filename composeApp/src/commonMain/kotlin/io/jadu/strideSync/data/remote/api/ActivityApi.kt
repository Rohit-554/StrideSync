package io.jadu.strideSync.data.remote.api

import io.jadu.strideSync.data.remote.dto.ActivityResponse
import io.jadu.strideSync.data.remote.dto.CreateActivityRequest
import io.jadu.strideSync.data.remote.dto.FeedItemResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val PATH_ACTIVITIES = "/activities"
private const val PATH_FEED = "/feed"

class ActivityApi(private val client: HttpClient) {

    suspend fun create(request: CreateActivityRequest): ActivityResponse =
        client.post(PATH_ACTIVITIES) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun getById(id: String): ActivityResponse =
        client.get("$PATH_ACTIVITIES/$id").body()

    suspend fun getMine(page: Int, size: Int): List<ActivityResponse> =
        client.get(PATH_ACTIVITIES) {
            parameter("page", page)
            parameter("size", size)
        }.body()

    suspend fun getFeed(page: Int, size: Int): List<FeedItemResponse> =
        client.get(PATH_FEED) {
            parameter("page", page)
            parameter("size", size)
        }.body()

    suspend fun delete(id: String) {
        client.delete("$PATH_ACTIVITIES/$id")
    }
}
