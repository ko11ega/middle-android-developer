package ru.skillbranch.skillarticles.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ru.skillbranch.skillarticles.data.local.entities.ArticlePersonalInfo
@Dao
interface ArticlePersonalInfosDao: BaseDao<ArticlePersonalInfo> {

    @Query("""
        SELECT * FROM article_personal_infos
    """)
    //- получить все записи ArticlePersonalInfo (ArticlePersonalInfosDao)
    fun findPersonalInfos(): LiveData<List<ArticlePersonalInfo>>

    @Query("""
        SELECT * FROM article_personal_infos
        WHERE article_id = :articleId
    """)
    //- получение ArticlePersonalInfo для конкретной статьи
    fun findPersonalInfos(articleId:String): LiveData<List<ArticlePersonalInfo>>


    @Transaction
    fun upsert(list: List<ArticlePersonalInfo>){
        insert(list)
            .mapIndexed{index, recordResult -> if(recordResult == -1L) list[index] else null}
            .filterNotNull()
            .also{ if(it.isNotEmpty()) update(it)}
    }

    @Query("""
        UPDATE article_personal_infos SET is_like = NOT is_like, updated_at =CURRENT_TIMESTAMP
        WHERE article_id = :articleId
    """)
    fun toggleLike(articleId: String): Int

    @Query("""
        UPDATE article_personal_infos SET is_bookmark = NOT is_bookmark, updated_at =CURRENT_TIMESTAMP
        WHERE article_id = :articleId
    """)
    fun toggleBookmark(articleId: String): Int

    @Transaction
    fun toggleBookmarkOrInsert(articleId: String){
        if(toggleBookmark(articleId) ==0 ) insert(ArticlePersonalInfo(articleId = articleId, isBookmark = true))
    }

    @Transaction
    fun toggleLikeOrInsert(articleId: String){
        if(toggleLike(articleId) ==0 ) insert(ArticlePersonalInfo(articleId = articleId, isLike = true))
    }
}