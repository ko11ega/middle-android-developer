package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.remote.res.ArticleContentRes
import java.util.*

fun ArticleData.toArticleContentRes(): ArticleContentRes {
    return ArticleContentRes(
        articleId = this.id,
        content = this.content,
        source = this.source,
        shareLink = this.shareLink,
        updatedAt = Date()
    )
}