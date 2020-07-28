package ru.skillbranch.skillarticles.data.local

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.models.AppSettings
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository

object PrefManager {
    internal val preferences : SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    val prefLiveData: MutableLiveData<AppSettings> = MutableLiveData<AppSettings>(AppSettings())

    var authorization: Boolean = false

    val isAuthLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)

    fun clearAll(){
        preferences.edit().clear().apply()
    }

    fun updatePrefLiveData(appSettings: AppSettings) {
        with( preferences.edit()){
            putBoolean("isDarkMode", appSettings.isDarkMode)
            putBoolean("isBigText", appSettings.isBigText)
            apply()
            commit()
        }
        val isDarkMode = preferences.getBoolean("isDarkMode", false)
        val isBigText = preferences.getBoolean("isBigText", false)
        prefLiveData.postValue(AppSettings(isDarkMode, isBigText))
    }

    fun getAppSettings(): LiveData<AppSettings> {
        return prefLiveData
    }

    fun isAuth(): MutableLiveData<Boolean> {
        return isAuthLiveData
    }

    fun setAuth(auth: Boolean): Unit {
        authorization = auth
        isAuthLiveData.postValue(authorization)

    }
}