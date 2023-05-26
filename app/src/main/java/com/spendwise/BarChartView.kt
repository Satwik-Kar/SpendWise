package com.spendwise

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.View
import android.widget.Toast
import java.lang.NullPointerException

class BarChartView(context: Context):View(context) {
    private var chartData :List<Int> = emptyList();
    private val rectF = RectF(730f,65f,750f,600f)
    private val paint = Paint()
    fun setData(listOfData:List<Int>){
        chartData = listOfData
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.strokeWidth = 10f
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.textSize = 50f

        if(canvas != null) {
            canvas.drawRoundRect(100f, 500f-100f, 180f, 600f, 30f, 30f, paint)
            canvas.drawRoundRect(200f, 500-0f, 280f, 600f, 30f, 30f, paint)
            canvas.drawRoundRect(300f, 400f, 380f, 600f, 30f, 30f, paint)
            canvas.drawRoundRect(400f, 500f, 480f, 600f, 30f, 30f, paint)
            canvas.drawRoundRect(500f, 300f, 580f, 600f, 30f, 30f, paint)
            canvas.drawRoundRect(600f, 100f, 680f, 600f, 30f, 30f, paint)

            canvas.drawRoundRect(rectF,23f,23f,paint)
            canvas.drawText("0",790f,600f,paint)
            canvas.drawText("100",790f,500f,paint)
            canvas.drawText("200",790f,400f,paint)
            canvas.drawText("300",790f,300f,paint)
            canvas.drawText("400",790f,200f,paint)
            canvas.drawText("500",790f,100f,paint)

        }else{
            throw NullPointerException("No Canvas")
        }

    }
}