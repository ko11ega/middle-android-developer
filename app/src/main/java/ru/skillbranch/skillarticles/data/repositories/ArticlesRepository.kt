package ru.skillbranch.skillarticles.data.repositories

import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import ru.skillbranch.skillarticles.data.LocalDataHolder
import ru.skillbranch.skillarticles.data.NetworkDataHolder
import ru.skillbranch.skillarticles.data.local.entities.ArticleItem
import java.lang.Thread.sleep

object ArticlesRepository {

    private val local = LocalDataHolder
    private val network = NetworkDataHolder

    fun allArticles(): ArticlesDataFactory =
        ArticlesDataFactory(ArticleStrategy.AllArticles(::findArticlesByRange))

    fun searchArticles(searchQuery: String) =
        ArticlesDataFactory(ArticleStrategy.SearchArticle(::searchArticlesByTitle, searchQuery))

    fun allBookmarked(): ArticlesDataFactory =
        ArticlesDataFactory(ArticleStrategy.BookmarkArticles(::findBookmarkArticles))

    fun searchBookmarkedArticles(searchQuery: String): ArticlesDataFactory =
        ArticlesDataFactory(ArticleStrategy.SearchBookmark(::searchBookmarkArticles, searchQuery))

    private fun findArticlesByRange(start: Int, size: Int) = local.LOCAL_ARTICLE_ITEMS
        .drop(start)
        .take(size)

    private fun findBookmarkArticles(start: Int, size: Int) = local.LOCAL_ARTICLE_ITEMS
        .asSequence()
        .filter { it.isBookmark }
        .drop(start)
        .take(size)
        .toList()

    private fun searchBookmarkArticles(start: Int, size: Int,  query: String) = local.LOCAL_ARTICLE_ITEMS
        .asSequence()
        .filter { it.isBookmark  && it.title.contains(query, true)  }
        .drop(start)
        .take(size)
        .toList()

    private fun searchArticlesByTitle(start: Int, size: Int, queryTitle: String) =
        local.LOCAL_ARTICLE_ITEMS
            .asSequence()
            .filter { it.title.contains(queryTitle, true) }
            .drop(start)
            .take(size)
            .toList()

    fun loadArticlesFromNetwork(start: Int, size: Int): List<ArticleItem> =
        network.NETWORK_ARTICLE_ITEMS
            .drop(start)
            .take(size)
            .apply { sleep(500) }

    fun insertArticlesToDb(articles: List<ArticleItem>) {
        local.LOCAL_ARTICLE_ITEMS.addAll(articles)
            .apply { sleep(100) }
    }

    fun updateBookmark(id: String, checked: Boolean) {
        val index = local.LOCAL_ARTICLE_ITEMS.indexOfFirst { it.id == id }
        if (index == -1) return
        local.LOCAL_ARTICLE_ITEMS[index] = local.LOCAL_ARTICLE_ITEMS[index].copy(isBookmark = checked)
    }


}

class ArticlesDataFactory(val strategy: ArticleStrategy) :
    DataSource.Factory<Int, ArticleItem>() {
    override fun create(): DataSource<Int, ArticleItem> = ArticleDataSource(strategy)
}


class ArticleDataSource(private val strategy: ArticleStrategy) :
    PositionalDataSource<ArticleItem>() {
    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<ArticleItem>
    ) {
        val result = strategy.getItems(params.requestedStartPosition, params.requestedLoadSize)
        callback.onResult(result, params.requestedStartPosition)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<ArticleItem>) {
        val result = strategy.getItems(params.startPosition, params.loadSize)
        callback.onResult(result)
    }
}

sealed class ArticleStrategy() {
    abstract fun getItems(start: Int, size: Int): List<ArticleItem>

    class AllArticles(
        private val itemProvider: (Int, Int) -> List<ArticleItem>
    ) : ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItem> =
            itemProvider(start, size)
    }

    class SearchArticle(
        private val itemProvider: (Int, Int, String) -> List<ArticleItem>,
        private val query: String
    ) : ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItem> =
            itemProvider(start, size, query)
    }

    class SearchBookmark(
        private val itemProvider: (Int, Int, String) -> List<ArticleItem>,
        private val query: String
    ) : ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItem> =
            itemProvider(start, size, query)
    }

    class BookmarkArticles(
        private val itemProvider: (Int, Int) -> List<ArticleItem>
    ) : ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItem> =
            itemProvider(start, size)
    }
}