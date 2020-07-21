package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.local.entities.ArticleCounts
import ru.skillbranch.skillarticles.data.remote.res.ArticleCountsRes
import java.util.*

fun ArticleCountsRes.toArticleCounts(): ArticleCounts {
    return ArticleCounts(
        articleId = this.articleId,
        likes = this.likes,
        comments = this.comments,
        readDuration = this.readDuration,
        updatedAt = Date()
    )
}
