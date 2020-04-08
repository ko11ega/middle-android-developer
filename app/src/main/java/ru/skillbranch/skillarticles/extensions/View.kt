package ru.skillbranch.skillarticles.extensions

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.widget.NestedScrollView

/*
setMarginOptionally
Необходимо реализовать функцию расширения (View.kt) добавляющую внешний отступ для View
+1
Реализуй функцию расширения
View.setMarginOptionally(left:Int = marginLeft, top : Int = marginTop, right : Int = marginRight, bottom : Int = marginBottom),
в качестве аргумента принимает значения внешних отступов View (margin) в пикселях
*/

fun View.setMarginOptionally(
    left:Int = marginLeft,
    top : Int = marginTop,
    right : Int = marginRight,
    bottom : Int = marginBottom)
{
    if(
        (left != marginLeft)||
        (top != marginTop)||
        (right != marginRight)||
        (bottom != marginBottom)
    ) {
        (layoutParams as? ViewGroup.MarginLayoutParams)?.run{
            leftMargin = left
            rightMargin = right
            topMargin = top
            bottomMargin = bottom
        }

    }
}