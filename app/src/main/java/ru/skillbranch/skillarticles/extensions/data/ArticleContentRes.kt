package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.local.entities.ArticleContent
import ru.skillbranch.skillarticles.data.remote.res.ArticleContentRes

fun ArticleContentRes.toArticleContent(): ArticleContent { // TODO +
    return ArticleContent(
    articleId = this.articleId,
    content = this.content,
    source = this.source,
    shareLink = this.shareLink,
    updatedAt = this.updatedAt
    )
}
