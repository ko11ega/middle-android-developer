package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.marginRight
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

/*
ArticleMenu scroll Behavior
Необходимо реализовать поведение скрытия ArticleMenu при скорлле
Реализуй поведение скрытия ArticleMenu при скорлле. при вертикальном скролле вверх (swipeUp)
ArticleMenu если открыто должено скрываться из области видимости пользователя,
при вертикальном скролле вниз (swipeDown) если ArticleMenu открыто то должено появляться
в области видимости пользователя
(Для Coordinator Layout необходимо использовать следующий идентификатор android:id="@+id/coordinator_container"
для ArticleSubmenu android:id="@+id/submenu"
для кнопки открытия меню в Bottombar android:id="@+id/btn_settings")
 */


class SubmenuBehavior<V : View>(context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<V>(context, attrs) {

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, axes: Int, type: Int
    ): Boolean {

        Log.d("M_SubmenuBehavior","onStartNestedScroll[target] ${target.javaClass}")
        Log.d("M_SubmenuBehavior","onStartNestedScroll[directTargetChild] ${directTargetChild.javaClass}")
        Log.d("M_SubmenuBehavior","onStartNestedScroll[child] ${child.javaClass}")
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL

    }


    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout, child: V, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int
    ) {
        Log.d("M_SubmenuBehavior","onNestedPreScroll[child] ${child.javaClass}")
        Log.d("M_SubmenuBehavior","onNestedPreScroll[child] ${child.marginRight}")

        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        //if (child.isVisible == true)
        //child.translationY = max(0f, min(child.height.toFloat(), child.translationY + dy))
        child.translationX = max(0f, min(child.width.toFloat()+child.marginRight, child.translationX + dy))
    }
}