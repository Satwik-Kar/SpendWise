package com.spendwise

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View
import android.widget.Toast

class BarChartView(context: Context):View(context) {
    private var chartData :List<Int> = emptyList();

    fun setData(listOfData:List<Int>){
        chartData = listOfData
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        canvas!!.drawRoundRect(100f,100f,180f,600f,30f,30f,paint)
        canvas.drawRoundRect(200f,100f,280f,600f,30f,30f,paint)
        canvas.drawRoundRect(300f,100f,380f,600f,30f,30f,paint)
        canvas.drawRoundRect(400f,100f,480f,600f,30f,30f,paint)
        canvas.drawRoundRect(500f,100f,580f,600f,30f,30f,paint)
        canvas.drawRoundRect(600f,100f,680f,600f,30f,30f,paint)


    }
}