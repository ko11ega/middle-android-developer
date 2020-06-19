package ru.skillbranch.skillarticles.viewmodels.articles

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.skillbranch.skillarticles.data.LocalDataHolder
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.data.repositories.ArticleStrategy
import ru.skillbranch.skillarticles.data.repositories.ArticlesDataFactory
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.Notify
import java.util.concurrent.Executors

class ArticlesViewModel(handle: SavedStateHandle) :
    BaseViewModel<ArticlesState>(handle, ArticlesState()) {
    val repository = ArticlesRepository
    private val listConfig by lazy {
        PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .setPrefetchDistance(30)
            .setInitialLoadSizeHint(50)
            .build()
    }
    private val listData = Transformations.switchMap(state) {
        when {
            it.isSearch && !it.searchQuery.isNullOrBlank() -> buildPagedList(
                repository.searchArticles(
                    it.searchQuery
                )
            )
            else -> buildPagedList(repository.allArticles())
        }
    }


    fun observeList(
        owner: LifecycleOwner,
        onChange: (list: PagedList<ArticleItemData>) -> Unit
    ) {
        listData.observe(owner, Observer {onChange(it)})
    }


    private fun buildPagedList(
        dataFactory: ArticlesDataFactory
    ): LiveData<PagedList<ArticleItemData>> {
        val builder = LivePagedListBuilder<Int, ArticleItemData> (
            dataFactory,
            listConfig
        )

        //if all articles
        if (dataFactory.strategy is ArticleStrategy.AllArticles) {
            builder.setBoundaryCallback(
                ArticlesBoundaryCallback(
                    ::zeroLoadingHandle,
                    ::itemAtEndHandle
                )
            )
        }

        return builder
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    private fun itemAtEndHandle(lastLoadArticle: ArticleItemData) {
        Log.e("ArticlesViewModel", "ItemAtEndHandle: ");
        viewModelScope.launch(Dispatchers.IO) {
            val items = repository.loadArticlesFromNetwork(
                start = lastLoadArticle.id.toInt().inc(),
                size = listConfig.pageSize
            )
            if (items.isNotEmpty()) {
                repository.insertArticlesToDb(items)
                //invalidate data in data source -> create new LiveData<PagedList>
                listData.value?.dataSource?.invalidate()
            }

            withContext(Dispatchers.Main) {
                notify(
                    Notify.TextMessage(
                        "Load from network articles from ${items.firstOrNull()?.id} " +
                                "to ${items.lastOrNull()?.id}"
                    )
                )
            }
        }
    }

    private fun zeroLoadingHandle() {
        Log.e("ArticlesViewModel", "zeroLoadingHandle: ");
        notify(Notify.TextMessage("Storage is empty"))
        viewModelScope.launch(Dispatchers.IO) {
            val items =
                repository.loadArticlesFromNetwork(
                    start = 0,
                    size = listConfig.initialLoadSizeHint
                )
            if (items.isNotEmpty()) {
                repository.insertArticlesToDb(items)
                //invalidate data in datasource -> create new LiveData<PagedList>
                listData.value?.dataSource?.invalidate()
            }
        }
    }

    fun handleSearch(query: String?) {
        query ?: return
        updateState { it.copy(searchQuery = query) }
    }

    fun handleSearchMode(isSearch: Boolean) {
        updateState { it.copy(isSearch = isSearch)}
    }

    /*
        Bookmarks
        Необходимо реализовать переключение isBookmark для статьи при клике по
        CheckableImageView (R.id.iv_bookmark) в ArticleItemView
        +1
        Реализуй переключение isBookmark для статьи при клике по CheckableImageView (R.id.iv_bookmark)
        в ArticleItemView для этого необходимо реализовать в ArticlesViewModel метод
        fun handleToggleBookmark(id: String, isChecked: Boolean) и метод
        fun updateBookmark(id: String, isChecked: Boolean) в ArticlesRepository
     */
    fun handleToggleBookmark(id: String, isChecked: Boolean){
        //LocalDataHolder.localArticleItems[id.toInt()] = LocalDataHolder.localArticleItems[id.toInt()].copy(isBookmark = !isChecked)
        repository.updateBookmark(id, !isChecked)
        //buildPagedList(repository.allArticles())
        listData.value?.dataSource?.invalidate()
    }
}

data class ArticlesState(
    val isSearch: Boolean =false,
    val searchQuery: String? = null,
    val isLoading: Boolean = true
): IViewModelState

class ArticlesBoundaryCallback(
    private val zeroLoadingHandle: () -> Unit,
    private val itemAtEndHandle: (ArticleItemData) -> Unit

) : PagedList.BoundaryCallback<ArticleItemData>() {
    override fun onZeroItemsLoaded() {
        //Storage is empty
        zeroLoadingHandle()
    }

    override fun onItemAtEndLoaded(itemAtEnd: ArticleItemData) {
        //user scroll down -> need load more items
        itemAtEndHandle(itemAtEnd)
    }
}























