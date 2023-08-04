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
import kotlin.math.pow
import kotlin.math.roundToInt

class LineGraphView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private var MAX_EXPENSE = 1000
        private const val GRAPH_PADDING = 55
        private const val LINE_STROKE_WIDTH = 4f
        private const val TEXT_SIZE = 28f
        private var LINE_COLOR = Color.TRANSPARENT
    }


    private val linePaint: Paint = Paint().apply {
        color = LINE_COLOR
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
    private var expenses: List<Double>? = null
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
        expenses = expensesData.map { it.toDouble() }
        months = monthsData
        invalidate()
    }

    fun setMaxExpense(maxExpense: Int) {
        MAX_EXPENSE = maxExpense
        invalidate()
    }

    fun setLineColor(color: Int) {
        LINE_COLOR = color
        linePaint.color = LINE_COLOR
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
            linePath.lineTo(x, y.toFloat())
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
            val expenseValue = padAmount(expensesList[i])
            canvas.drawText(expenseValue, x, (y - TEXT_SIZE).toFloat(), textPaint)

            // Draw month name below the peak
            val month = monthsList[i]
            canvas.drawText(month, x, (y + 1 * TEXT_SIZE).toFloat(), textPaint)
        }
    }

    private fun padAmount(amount: Double): String {
        return when {
            amount >= 1000 -> {

                val decimalPlaces = 2.0
                val roundedVal =
                    ((amount / 1000) * 10.0.pow(decimalPlaces)).roundToInt() / 10.0.pow(
                        decimalPlaces
                    )

                return "${roundedVal}T"


            }

            else -> amount.toString()
        }
    }

}
