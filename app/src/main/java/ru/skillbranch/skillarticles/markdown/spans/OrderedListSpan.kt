package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting

/*
*OrderedListSpan
Необходимо реализовать RegEx выражения для разбора элементов markdown разметки
(упорядоченный элемент списка) и пользовательский Span реализующий интерфейс LeadingMarginSpan
+2
Реализуй RegEx выражение для поиска упорядоченного элемента списка в markdown разметке
соответствующий паттерну:
1. text //valid
2. text //valid
3. text //valid
 1. text //invalid
A text //invalid
1 //invalid
И реализуй пользовательский Span (OrderedListSpan.kt) для отрисовки упорядоченного элемента списка
(убедиться в правильности отрисовки при помощи приложенных mock тестов)
 */

class OrderedListSpan(
    @Px
    private val gapWidth: Float,
    private val order: String,
    @ColorInt
    private val orderColor: Int
) : LeadingMarginSpan {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)

    override fun getLeadingMargin(first: Boolean): Int {
        //return (4*bulletRadius+gapWidth).toInt()
        return (order.length.inc() * gapWidth).toInt()
    }

    override fun drawLeadingMargin(
        canvas: Canvas, paint: Paint, currentMarginLocation: Int, paragraphDirection: Int,
        lineTop: Int, lineBaseline: Int, lineBottom: Int, text: CharSequence?, lineStart: Int,
        lineEnd: Int, isFirstLine: Boolean, layout: Layout?
    ) {

        if (isFirstLine) {
            // order
            paint.withCustomColor {
                canvas.drawText(
                    order,//+" ",
                    //0,
                    //(order.length).toInt(),
                    gapWidth,
                    lineBaseline.toFloat(),
                    paint
                )
            }
        }
/*
        paint.withCustomColor {
            canvas.drawCircle(
                gapWidth + currentMarginLocation + gapWidth,
                (lineTop + lineBottom) / 2f,
                gapWidth,
                paint
            )
        }
*/
/*
        if(isFirstLine){
            // order
            paint.withCustomColor {
                canvas.drawText(order, 0, (order.length.inc() * gapWidth).toInt(), 0f, lineBottom.toFloat(), paint)
            }

            // text
            paint.withCustomColor {
                canvas.drawText(text, start, end, textStart, y.toFloat(), paint)
            }



            paint.withCustomColor {
                canvas.drawCircle(
                    gapWidth + currentMarginLocation + gapWidth,
                    (lineTop + lineBottom) / 2f,
                    gapWidth,
                    paint
                )
            }
        }
 */
    }

    private inline fun Paint.withCustomColor(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style

        color = orderColor
        style = Paint.Style.FILL

        block()

        color = oldColor
        style = oldStyle

    }


}