package io.jadu.strideSync.domain.model

data class FeedItem(
    val activity: Activity,
    val user: User,
    val kudosCount: Int,
    val commentCount: Int,
    val hasKudosed: Boolean
)
