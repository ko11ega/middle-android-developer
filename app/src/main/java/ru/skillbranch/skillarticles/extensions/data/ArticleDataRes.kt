package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.local.entities.Article
import ru.skillbranch.skillarticles.data.remote.res.ArticleDataRes
import java.util.*

fun ArticleDataRes.toArticle(): Article {
    return Article(
        id = this.id,
        title = this.title,
        description = this.description,
        author = this.author,
        categoryId = this.category.categoryId,
        poster =  this.poster,
        date = date,
        updatedAt = Date()
    )
}