package ru.skillbranch.skillarticles.viewmodels.article

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.data.repositories.clearContent
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify


class ArticleViewModel(
    handle: SavedStateHandle,
    private val articleId:String
) :
    BaseViewModel<ArticleState>(
        handle,
        ArticleState()
    ), IArticleViewModel{
    private val repository = ArticleRepository
    private var clearContent:String? =null

    init {
        //subscribe on mutable data
        subscribeOnDataSource(getArticleData()){ article,state ->
            article ?: return@subscribeOnDataSource null
            Log.e("ArticleViewModel","author: ${article.author}")
            state.copy (
                shareLink =article.shareLink,
                title =article.title,
                category =article.category,
                categoryIcon =article.categoryIcon,
                date = article.date.format(),
                author = article.author
            )
        }

        subscribeOnDataSource(getArticleContent()) { content, state ->
            content ?: return@subscribeOnDataSource null
            state.copy (
                isLoadingContent = false,
                content = content
            )
        }

        subscribeOnDataSource(getArticlePersonalInfo()) {info, state ->
            info ?: return@subscribeOnDataSource null
            state.copy(
                isBookmark = info.isBookmark,
                isLike =info.isLike
            )
        }

        subscribeOnDataSource(repository.getAppSettings()) {settings, state ->
            state.copy(
                isDarkMode = settings.isDarkMode,
                isBigText = settings.isBigText
            )
        }

        subscribeOnDataSource(repository.isAuth()) { auth, state ->
            state.copy(isAuth = auth)
        }
    }

    // load text from network
    override fun getArticleContent(): LiveData<List<MarkdownElement>?> {
        return repository.loadArticleContent(articleId)
    }

    //load data from db
    override fun getArticleData(): LiveData<ArticleData?>{
        return repository.getArticle(articleId)
    }

    // Получение пользовательской информации о статье из базы данных
    override fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?>{
        return repository.loadArticlePersonalInfo(articleId)
    }

    //Получение настроек приложения
    override fun handleNightMode(){
        val settings =currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }

    override fun handleUpText(){
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    override fun handleDownText(){
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }


    // personal article info
    override fun handleBookmark(){
        val info = currentState.toArticlePersonalInfo()
        repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))

        val msg = if (currentState.isBookmark) "Add to bookmarks" else "Remove from bookmarks"
        notify(Notify.TextMessage(msg))
    }

    override fun handleLike(){
        Log.e("ArticleViewModel", "handle like:");
        val isLiked =currentState.isLike
        val toggleLike = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike =!info.isLike))
        }

        toggleLike()

        val msg = if (!isLiked) Notify.TextMessage("Mark is liked")
        else {
            Notify.ActionMessage(
                "Don`t like it anymore", //message
                "No, still like it", //action label on snackbar
                toggleLike //handler function, if press "No, still like it"
            )
        }

        notify(msg)
    }


    // not implemented
    override fun handleShare(){
        val msg = "Share is not implemented"
        notify(Notify.ErrorMessage(msg,"OK", null))
    }


    //session state
    override fun handleToggleMenu(){
        updateState { it.copy(isShowMenu = !it.isShowMenu) }
    }

    override fun handleSearchMode(isSearch: Boolean){
        updateState { it.copy(isSearch = isSearch, isShowMenu = false, searchPosition = 0)}
    }

    override fun handleSearch(query: String?){
        query ?: return
        if(clearContent ==null && currentState.content.isNotEmpty())
            clearContent = currentState.content.clearContent()

        val result = clearContent
            .indexesOf(query)
            .map {it to it + query.length }
        updateState { it.copy(searchQuery = query, searchResults = result!!, searchPosition = 0 )}
    }

    override fun handleUpResult() {
        updateState { it.copy(searchPosition = it.searchPosition.dec()) }
    }

    override fun handleDownResult() {
        updateState { it.copy(searchPosition = it.searchPosition.inc()) }
    }

    override fun handleCopyCode() {
        notify(Notify.TextMessage("Code copy to clipboard"))
    }

    override fun handleSendComment() {
        if (!currentState.isAuth) navigate(NavigationCommand.StartLogin())
        // TODO send comment
    }
}

data class ArticleState(
    val isAuth:Boolean = false, //user was authorized
    val isLoadingContent: Boolean =true, //content is loading
    val isLoadingReviews: Boolean =true, //reviews are loading
    val isLike: Boolean =false, // liked
    val isBookmark: Boolean = false, // bookmarked
    val isShowMenu: Boolean = false,
    val isBigText: Boolean = false,
    val isDarkMode: Boolean = false,
    val isSearch:Boolean =false, //search mode
    val searchQuery: String? = null,
    val searchResults: List<Pair<Int,Int>> = emptyList(), // search results
    val searchPosition: Int = 0, //current found position
    val shareLink: String? = null,
    val title: String? = null,
    val category: String? = null,
    val categoryIcon: Any? = null,
    val date: String? = null,// publication date
    val author: Any? = null, //author of article
    val poster: String? = null, //cover of article
    val content: List<MarkdownElement> = emptyList(),//content
    val reviews: List<Any> = emptyList()
) : IViewModelState {
    override fun save(outState: SavedStateHandle) {
        outState.set("isSearch", isSearch)
        outState.set("searchQuery", searchQuery)
        outState.set("searchResults", searchResults)
        outState.set("searchPosition", searchPosition)
    }

    override fun restore(savedState: SavedStateHandle): ArticleState {
        return copy(
            isSearch = savedState["isSearch"] ?: false,
            searchQuery =  savedState["searchQuery"],
            searchResults = savedState["searchResults"] ?: emptyList(),
            searchPosition = savedState["searchPosition"] ?: 0
        )
    }
}


fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
    var result : MutableList<Int> = mutableListOf()
    var matchIndex : Int = -1
    if(this == null) return result
    while(this.indexOf(substr, matchIndex, ignoreCase)>0) {
        matchIndex = this.indexOf(substr, matchIndex+1, ignoreCase)
        if (matchIndex==-1) break
        result.add(matchIndex)
    }
    return result
}