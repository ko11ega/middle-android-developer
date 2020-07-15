package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.marginRight
import ru.skillbranch.skillarticles.ui.custom.ArticleSubmenu
import ru.skillbranch.skillarticles.ui.custom.Bottombar
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

/*

class SubmenuBehavior<V : View>() : CoordinatorLayout.Behavior<V>() { //TODO

    constructor(context: Context, attrs: AttributeSet): this()    // TODO

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


        //if (child.isVisible == true)
        //child.translationY = max(0f, min(child.height.toFloat(), child.translationY + dy))
        child.translationX = max(0f, min(child.width.toFloat()+child.marginRight, child.translationX + dy))
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)

    }
}

 */


class SubmenuBehavior : CoordinatorLayout.Behavior<ArticleSubmenu>() {
    // Set view as dependent on bottombar
    override fun layoutDependsOn(parent: CoordinatorLayout, child: ArticleSubmenu, dependency: View): Boolean {
        return dependency is Bottombar
    }

    // Will be called if dependent view has been changed
    override fun onDependentViewChanged(parent: CoordinatorLayout, child: ArticleSubmenu, dependency: View): Boolean {
        return if (dependency is Bottombar && dependency.translationY >= 0) {
            animate(child, dependency)
            true
        } else false
    }

    private fun animate(child: ArticleSubmenu, dependency: Bottombar) {
        // Calculate the ratio between height and width
        val fraction = dependency.translationY / dependency.minHeight

        // Set translationX (horizontal location of this view relative to its left position)
        child.translationX = (child.width + child.marginRight) * fraction

        Log.d("My_SubmenuBehavior", "fraction: ${fraction}, translationX: ${child.translationX}")
    }
}