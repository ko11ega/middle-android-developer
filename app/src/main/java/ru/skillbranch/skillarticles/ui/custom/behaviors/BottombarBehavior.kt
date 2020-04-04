package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import kotlin.math.max
import kotlin.math.min

/*
*Bottombar scroll Behavior
Необходимо реализовать поведение скрытия Bottombar при скорлле
Реализуй поведение скрытия Bottombar при скорлле. при вертикальном скролле вверх (swipeUp)
Bottombar должен скрываться из области видимости пользователя, при вертикальном скролле вниз (swipeDown)
должен появляться в области видимости пользователя
(Для Coordinator Layout необходимо использовать следующий идентификатор android:id="@+id/coordinator_container"
для Bottombar android:id="@+id/bottombar")
 */

class BottombarBehavior<V : View>(context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<V>(context, attrs) {

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, axes: Int, type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout, child: V, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int
    ) {
        child.translationY = max(0f, min(child.height.toFloat(), child.translationY + dy))
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }
}

/*
-         │   │   ├── behaviors
-         │   │   │   ├── BottombarBehavior.kt
-         │   │   │   └── SubmenuBehavior.kt
*/
