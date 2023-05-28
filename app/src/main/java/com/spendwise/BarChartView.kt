package com.spendwise

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View

class BarChartView(context: Context, listOfData: List<Float>) : View(context) {
    private var chartData: List<Float> = listOfData
    private val paint = Paint()


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.strokeWidth = 10f
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.textSize = 40f


        if (canvas != null) {
            val barHeight1 = (chartData[0] / 500f * height)
            val invert1 = height - barHeight1
            val barHeight2 = (chartData[1] / 500f * height)
            val invert2 = height - barHeight2;
            val barHeight3 = (chartData[2] / 500f * height)
            val invert3 = height - barHeight3;
            val barHeight4 = (chartData[3] / 500f * height)
            val invert4 = height - barHeight4;
            val barHeight5 = (chartData[4] / 500f * height)
            val invert5 = height - barHeight5;
            val barHeight6 = (chartData[5] / 500f * height)
            val invert6 = height - barHeight6;
            val barHeight500 = (500f / 500f * height)
            val invert500 = height - barHeight500
            val barHeight400 = (400f / 500f * height)
            val invert400 = height - barHeight400
            val barHeight300 = (300f / 500f * height)
            val invert300 = height - barHeight300
            val barHeight200 = (200f / 500f * height)
            val invert200 = height - barHeight200
            val barHeight100 = (100f / 500f * height)
            val invert100 = height - barHeight100
            val barHeight0 = (0f / 500f * height)
            val invert0 = height - barHeight0
            canvas.drawRoundRect(100f, invert1, 180f, invert0, 30f, 30f, paint)
            canvas.drawRoundRect(200f, invert2, 280f, invert0, 30f, 30f, paint)
            canvas.drawRoundRect(300f, invert3, 380f, invert0, 30f, 30f, paint)
            canvas.drawRoundRect(400f, invert4, 480f, invert0, 30f, 30f, paint)
            canvas.drawRoundRect(500f, invert5, 580f, invert0, 30f, 30f, paint)
            canvas.drawRoundRect(600f, invert6, 680f, invert0, 30f, 30f, paint)

            val rectF = RectF(730f, invert500, 750f, invert0)
            canvas.drawRoundRect(rectF, 23f, 23f, paint)
            canvas.drawText("500", 790f, invert500, paint)
            canvas.drawText("400", 790f, invert400, paint)
            canvas.drawText("300", 790f, invert300, paint)
            canvas.drawText("200", 790f, invert200, paint)
            canvas.drawText("100", 790f, invert100, paint)
            canvas.drawText("0", 790f, invert0, paint)

        } else {
            throw NullPointerException("No Canvas")
        }

    }
}