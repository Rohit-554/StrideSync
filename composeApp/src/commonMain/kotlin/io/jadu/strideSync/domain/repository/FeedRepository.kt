package io.jadu.strideSync.domain.repository

import io.jadu.strideSync.domain.model.FeedItem
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    suspend fun refreshFeed(): Result<List<FeedItem>>

    suspend fun loadFeedPage(page: Int, size: Int): Result<List<FeedItem>>

    fun observeFeed(): Flow<List<FeedItem>>
}
