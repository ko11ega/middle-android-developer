package ru.skillbranch.skillarticles.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.NetworkDataHolder.content
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format
import java.util.*

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

class ArticleViewModel(private val articleId:String):BaseViewModel<ArticleState>(ArticleState()){
    private val repository = ArticleRepository
    private var menuIsShown:Boolean = false
    private var bottomBarIsShown:Boolean = true

    init {
        //subscribe on mutable data
        subscribeOnDataSource(getArticleData()){ article,state ->
            article ?: return@subscribeOnDataSource null
            state.copy (
                shareLink =article.shareLink,
                title =article.title,
                author = article.author,
                category =article.category,
                categoryIcon =article.categoryIcon,
                date = article.date.format()
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

        //subscribe on settings
        subscribeOnDataSource(repository.getAppSettings()) {settings, state ->
            state.copy(
                isDarkMode = settings.isDarkMode,
                isBigText = settings.isBigText
            )
        }
    }


    /**
     * Получение полной информации о статье из сети
     * (или базы данных если она сохранена, наличие статьи в базе не надо реализовывать в данном уроке)
     */
    fun getArticleContent(): LiveData<List<Any>?>{
        return repository.loadArticleContent(articleId)
    }

    /**
     * Получение краткой информации о статье из базы данных
     */
    fun getArticleData(): LiveData<ArticleData?>{
        return repository.getArticle(articleId)
    }

    /**
     * Получение пользовательской информации о статье из базы данных
     */
    fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?>{
        return repository.loadArticlePersonalInfo(articleId)
    }

    /**
     * обрабока нажатия на кнопку btn_settings
     * необходимо отобразить или скрыть меню в соответствии с текущим состоянием
     */
    fun handleToggleMenu(){
        updateState { state ->
            state.copy(isShowMenu = !state.isShowMenu).also{ menuIsShown = !state.isShowMenu}
        }
    }

    /**
     * Получение настроек приложения
     */
    fun handleNightMode(){
        val settings =currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }


    /**
     * Обработка нажатия на btn_text_up (увеличение шрифта текста)
     * необходимо увеличить шрифт до значения 18
     */
    fun handleUpText(){
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    /**
     * Обработка нажатия на btn_text_down (стандартный размер шрифта)
     * необходимо установить размер шрифта по умолчанию 14
     */
    fun handleDownText(){
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }

    /**
     * добавление/удалние статьи в закладки, обрабока нажатия на кнопку btn_bookmark
     * необходимо отобразить сообщение пользователю "Add to bookmarks" или "Remove from bookmarks"
     * в соответствии с текущим состоянием
     */
    fun handleBookmark(){
        val info = currentState.toArticlePersonalInfo()
        repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))

        val msg = if (currentState.isBookmark) "Add to bookmarks" else "Remove from bookmarks"
        notify(Notify.TextMessage(msg))


    }

    /**
     * добавление/удалние статьи в понравившееся, обрабока нажатия на кнопку btn_like
     * необходимо отобразить сообщение пользователю (Notify.ActionMessage) "Mark is liked" или
     * "Don`t like it anymore"  в соответствии с текущим состоянием.
     * если пользователь убрал Like необходимо добавить  actionLabel в снекбар
     * "No, still like it" при нажатиии на который состояние вернется к isLike = true
     */
    fun handleLike(){
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

    /**
     * поделиться статьей, обрабока нажатия на кнопку btn_share
     * необходимо отобразить сообщение с ошибкой пользователю (Notify.ErrorMessage) "Share is not implemented"
     * и текстом errLabel "OK"
     */
    fun handleShare(){
        val msg = "Share is not implemented"
        notify(Notify.ErrorMessage(msg,"OK", null))
    }

    fun hideMenu(){
        updateState{ it.copy(isShowMenu = false)}
    }

    fun showMenu(){
        updateState{ it.copy(isShowMenu = menuIsShown)}
    }


    /**
     * обрабока перехода в режим поиска searchView
     * при нажатии на пункту меню тулбара необходимо отобразить searchView и сохранить состояние при
     * изменении конфигурации (пересоздании активити)
     */
    //fun handleIsSearch(isSearch: Boolean){
    fun handleSearchMode(isSearch: Boolean){
        updateState { it.copy(isSearch = isSearch, isShowMenu = false, searchPosition = 0)}
    }

    /**
     * обрабока поискового запроса, необходимо сохранить поисковый запрос и отображать его в
     * searchView при изменении конфигурации (пересоздании активити)
     */
    //fun handleSearchQuery(query: String?){
    fun handleSearch(query: String?){
        query ?: return
        val result = (currentState.content.firstOrNull() as? String)
            ?.indexesOf(query)
            ?.map {it to it + query.length }
        updateState { it.copy(searchQuery = query, searchResults = result!!, searchPosition = 0 )}
    }

    /**
     * обрабока поискового запроса, необходимо сохранить поисковый запрос и отображать его в
     * searchView при изменении конфигурации (пересоздании активити)
     */
    //fun handleSearch(query: String?)
    fun handleSearchQuery(query: String?){
        updateState { it.copy(searchQuery = query)}
    }

    /**
     * обрабока перехода в режим поиска searchView
     * при нажатии на пункту меню тулбара необходимо отобразить searchView и сохранить состояние при
     * изменении конфигурации (пересоздании активити)
     */
    //fun handleSearchMode(isSearch: Boolean)
    fun handleIsSearch(isSearch: Boolean){
        updateState { it.copy(isSearch = isSearch)}
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
    val content: List<Any> = emptyList(),//content
    val reviews: List<Any> = emptyList()
)