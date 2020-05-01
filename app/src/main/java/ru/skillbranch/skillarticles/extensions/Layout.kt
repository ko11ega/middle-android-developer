package ru.skillbranch.skillarticles.extensions

import android.view.View

fun View.setPaddingOptionally(
    left: Int = paddingLeft,
    right: Int = paddingRight,
    top: Int = paddingTop,
    bottom: Int = paddingBottom
){
    setPadding(left,top,right,bottom)
}