package com.spendwise

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View

class BarChartView(context: Context, listOfData: List<Float>,listOfLabels:List<String>) : View(context) {
    private var chartData: List<Float> = listOfData
    private var chartDataLabels: List<String> = listOfLabels

    private val paint = Paint()
    val rect = RectF()
    val adjustMent = 80f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.strokeWidth = 10f
        paint.color = resources.getColor(R.color.app_theme);
        paint.style = Paint.Style.FILL
        paint.textSize = 40f
        paint.textAlign = Paint.Align.CENTER
        paint.isFakeBoldText = true

        if (canvas != null) {
            // for labels in the chart
            val barHeight500 = (500f / (500f) * height)
            val invert500 = height - barHeight500
            val barHeight400 = (400f / (500f)* height)
            val invert400 = height - barHeight400
            val barHeight300 = (300f / (500f) * height)
            val invert300 = height - barHeight300
            val barHeight200 = (200f / (500f) * height)
            val invert200 = height - barHeight200
            val barHeight100 = (100f / (500f) * height)
            val invert100 = height - barHeight100
            val barHeight0 = (0f / (500f) * height)
            val invert0 = height - barHeight0
            // for bars in the chart
            val barHeight1 = ((chartData[0] - 30) / 500f * height)
            val invert1 = height - barHeight1
            val barHeight2 = ((chartData[1]- 30) / 500f * height)
            val invert2 = height - barHeight2
            val barHeight3 = ((chartData[2]- 30) / 500f * height)
            val invert3 = height - barHeight3
            val barHeight4 = ((chartData[3]- 30 )/ 500f * height)
            val invert4 = height - barHeight4
            val barHeight5 = ((chartData[4]- 30) / 500f * height)
            val invert5 = height - barHeight5
            canvas.drawRoundRect(100f, invert1, 180f, invert0, 24f, 24f, paint)
            canvas.drawRoundRect(200f, invert2, 280f, invert0, 24f, 24f, paint)
            canvas.drawRoundRect(300f, invert3, 380f, invert0, 24f, 24f, paint)
            canvas.drawRoundRect(400f, invert4, 480f, invert0, 24f, 24f, paint)
            canvas.drawRoundRect(500f, invert5, 580f, invert0, 24f, 24f, paint)

            canvas.drawText(chartDataLabels[0], 100f, invert1-20, paint)
            canvas.drawText(chartDataLabels[1], 200f, invert2-20, paint)
            canvas.drawText(chartDataLabels[2], 300f, invert3-20, paint)
            canvas.drawText(chartDataLabels[3], 400f, invert4-20, paint)
            canvas.drawText(chartDataLabels[4], 500f, invert5-20, paint)


            rect.left = 730f
            rect.top = invert500 + 40
            rect.right = 750f
            rect.bottom = invert0
            canvas.drawRoundRect(rect, 23f, 23f, paint)
            canvas.drawText("- 500", 790f, invert500+adjustMent, paint)
            canvas.drawText("- 400", 790f, invert400+adjustMent, paint)
            canvas.drawText("- 300", 790f, invert300+adjustMent, paint)
            canvas.drawText("- 200", 790f, invert200+adjustMent, paint)
            canvas.drawText("- 100", 790f, invert100+adjustMent, paint)


        } else {
            throw NullPointerException("No Canvas")
        }

    }


}