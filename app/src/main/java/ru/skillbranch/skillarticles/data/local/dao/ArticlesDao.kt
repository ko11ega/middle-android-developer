package ru.skillbranch.skillarticles.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import ru.skillbranch.skillarticles.data.local.entities.Article
import ru.skillbranch.skillarticles.data.local.entities.ArticleItem

@Dao
interface ArticlesDao : BaseDao<Article>{

    @Transaction
    fun upsert(list: List<Article>){
        insert(list)
            .mapIndexed{index, recordResult -> if(recordResult == -1L) list[index] else null }
            .filterNotNull()
            .also { if(it.isNotEmpty()) update(it) }
    }

    @Query("""
        SELECT * FROM articles
    """)
    fun findArticles(): List<Article>

    @Query("""
        SELECT * FROM articles
        WHERE id = :id
    """)
    fun findArticleById(id: String): Article

    @Query("""
        SELECT * FROM ArticleItem
    """)
    fun findArticleItems(): List<ArticleItem>

    @Delete
    fun delete(article: Article)

    @Query("""
        SELECT * FROM ArticleItem
        WHERE category_id IN (:categoryIds)
    """)
    fun findArticleItemsByCategoryIds(categoryIds : List<String>): List<ArticleItem>

}