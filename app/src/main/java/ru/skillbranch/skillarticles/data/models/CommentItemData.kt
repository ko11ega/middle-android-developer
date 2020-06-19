package ru.skillbranch.skillarticles.data.models

import android.R
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import java.util.*


data class CommentItemData(
    val id: String,
    val articleId: String ,
    val user : User,
    val body: String,
    val date: Date,
    val slug:String,
    val answerTo:String? = null
) {

}