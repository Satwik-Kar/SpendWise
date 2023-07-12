package com.spendwise

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View


class LineGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private var MAX_EXPENSE = 1000 // Maximum expense value for scaling
        private const val GRAPH_PADDING = 25 // Padding around the graph
        private const val LINE_STROKE_WIDTH = 1f // Stroke width of the line
        private const val TEXT_SIZE = 28f // Text size for expense values and month names
    }

    private val linePaint: Paint = Paint().apply {
        color = resources.getColor(R.color.app_theme_transparent)
        strokeWidth = LINE_STROKE_WIDTH
        style = Paint.Style.STROKE
    }
    private val gradientPaint: Paint = Paint()
    private val textPaint: Paint = Paint().apply {
        color = Color.BLACK
        textSize = TEXT_SIZE
        textAlign = Paint.Align.CENTER
    }
    private val linePath: Path = Path()
    private var expenses: List<Int>? = null
    private var months: List<String>? = null

    init {
        gradientPaint.shader = LinearGradient(
            0f,
            0f,
            0f,
            height.toFloat(),
            resources.getColor(R.color.app_theme_transparent),
            resources.getColor(R.color.app_theme_transparent),
            Shader.TileMode.CLAMP
        )
    }

    fun setData(expensesData: ArrayList<String>, monthsData: ArrayList<String>) {
        expenses = expensesData.map { it.toInt() }
        months = monthsData
        invalidate()
    }

    fun setMaxExpense(maxExpense: Int) {
        MAX_EXPENSE = maxExpense
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val expensesList = expenses
        val monthsList = months
        if (expensesList == null || monthsList == null || expensesList.size < 2 || monthsList.size < 2) {
            return
        }

        val width = width
        val height = height
        val xInterval = (width - 2 * GRAPH_PADDING).toFloat() / (expensesList.size - 1)
        val yScale = (height - 2 * GRAPH_PADDING).toFloat() / MAX_EXPENSE

        // Draw line path
        linePath.reset()
        linePath.moveTo(GRAPH_PADDING.toFloat(), (height - GRAPH_PADDING).toFloat())

        for (i in expensesList.indices) {
            val x = GRAPH_PADDING.toFloat() + i * xInterval
            val y = (height - GRAPH_PADDING).toFloat() - expensesList[i] * yScale
            linePath.lineTo(x, y)
        }

        canvas.drawPath(linePath, linePaint)

        // Draw gradient
        val gradientPath = Path(linePath)
        val bounds = RectF()
        linePath.computeBounds(bounds, true)
        gradientPath.lineTo(bounds.right, height.toFloat())
        gradientPath.lineTo(bounds.left, height.toFloat())
        gradientPath.close()

        canvas.drawPath(gradientPath, gradientPaint)

        // Draw expense values and month names
        for (i in expensesList.indices) {
            val x = GRAPH_PADDING.toFloat() + i * xInterval
            val y = (height - GRAPH_PADDING).toFloat() - expensesList[i] * yScale

            // Draw expense value above the peak
            val expenseValue = expensesList[i].toString()
            canvas.drawText(expenseValue, x, y - TEXT_SIZE, textPaint)

            // Draw month name below the peak
            val month = monthsList[i]
            canvas.drawText(month, x, y + 2 * TEXT_SIZE, textPaint)
        }
    }
}
