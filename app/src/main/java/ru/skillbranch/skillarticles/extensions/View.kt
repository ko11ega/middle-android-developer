package ru.skillbranch.skillarticles.extensions

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.navigation.NavDestination
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
selectDestination
Необходимо реализовать extension функцию для отображения текущего пункта меню BottomNavigationView
соответствующему NavDestination
+1
Реализуй BottomNavigationView.selectDestination(destination: NavDestination) ддля отображения
текущего пункта меню BottomNavigationView соответствующему NavDestination.
Если destination является потомком destination top уровня
(@+id/nav_articles, @+id/nav_profile, @+id/nav_bookmarks, @+id/nav_transcriptions)
то соответствующий пункт меню в BottomNavigationView должен быть в состоянии selected
*/

fun BottomNavigationView.selectDestination(destination: NavDestination){ //TODO
    if (destination.parent!=null){
        if (destination.parent!!.parent == null){ // dest потомка top уровня

            this.menu.forEach { item -> item.isChecked = false }
            println("this.menu: ${this.menu.findItem(destination.id)} ${this.menu.findItem(destination.id).isChecked}")
            this.menu.findItem(destination.id).isChecked = true
            println("this.menu: ${this.menu.findItem(destination.id)} ${this.menu.findItem(destination.id).isChecked}")
            println("selectDestination: ${destination}")
        }
    }
}


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