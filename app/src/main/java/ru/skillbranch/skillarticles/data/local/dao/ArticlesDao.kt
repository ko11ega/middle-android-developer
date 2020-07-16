package ru.skillbranch.skillarticles.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import ru.skillbranch.skillarticles.data.local.entities.Article

@Dao
interface ArticlesDao : BaseDao<Article>{

    //TODO upsert

    @Query("""
        SELECT * FROM articles
    """)
    fun findArticles(): List<Article>

    @Query("""
        SELECT * FROM articles
        WHERE id = :id
    """)
    fun findArticleById(id: String): Article

}