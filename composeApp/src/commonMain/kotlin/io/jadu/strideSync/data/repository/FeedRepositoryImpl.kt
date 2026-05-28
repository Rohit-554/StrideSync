package io.jadu.strideSync.data.repository

import io.jadu.strideSync.data.local.dao.FeedDao
import io.jadu.strideSync.data.local.entity.CachedFeedEntity
import io.jadu.strideSync.data.remote.api.ActivityApi
import io.jadu.strideSync.data.remote.dto.FeedItemResponse
import io.jadu.strideSync.data.remote.dto.UserResponse
import io.jadu.strideSync.domain.model.FeedItem
import io.jadu.strideSync.domain.model.User
import io.jadu.strideSync.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val FEED_PAGE_SIZE = 20
private const val FEED_PAGE = 0

class FeedRepositoryImpl(
    private val activityApi: ActivityApi,
    private val feedDao: FeedDao
) : FeedRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun refreshFeed(): Result<List<FeedItem>> = runCatching {
        val response = activityApi.getFeed(page = FEED_PAGE, size = FEED_PAGE_SIZE)
        val cachedEntities = response.toCachedEntities()
        feedDao.clearAll()
        feedDao.insertAll(cachedEntities)
        response.map { it.toDomain() }
    }.onFailure { error ->
        println("StrideSync debug: feed refresh failed: ${error::class.simpleName}: ${error.message}")
    }

    override suspend fun loadFeedPage(page: Int, size: Int): Result<List<FeedItem>> = runCatching {
        val response = activityApi.getFeed(page = page, size = size)
        if (page == FEED_PAGE) {
            feedDao.clearAll()
        }
        feedDao.insertAll(response.toCachedEntities())
        response.map { it.toDomain() }
    }.onFailure { error ->
        println("StrideSync debug: feed page load failed: ${error::class.simpleName}: ${error.message}")
    }

    private fun List<FeedItemResponse>.toCachedEntities(): List<CachedFeedEntity> {
        val cachedAt = kotlin.time.Clock.System.now().toEpochMilliseconds()
        return map { item ->
            CachedFeedEntity(
                id = item.activity.id,
                jsonPayload = json.encodeToString(item),
                cachedAt = cachedAt
            )
        }
    }

    override fun observeFeed(): Flow<List<FeedItem>> =
        feedDao.getPage().map { entities ->
            entities.mapNotNull { entity ->
                runCatching {
                    json.decodeFromString<FeedItemResponse>(entity.jsonPayload).toDomain()
                }.getOrNull()
            }
        }
}

internal fun FeedItemResponse.toDomain(): FeedItem = FeedItem(
    activity = activity.toDomain(),
    user = user.toDomain(),
    kudosCount = kudosCount,
    commentCount = commentCount,
    hasKudosed = hasKudosed
)

internal fun UserResponse.toDomain(): User = User(
    id = id,
    displayName = displayName,
    email = email,
    avatarUrl = avatarUrl
)
