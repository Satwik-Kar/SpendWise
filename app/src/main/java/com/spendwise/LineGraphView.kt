package com.spendwise

import android.annotation.SuppressLint
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
        private const val MAX_EXPENSE = 1000
        private const val GRAPH_PADDING = 5
        private const val LINE_STROKE_WIDTH = 1f
    }

    private val linePaint: Paint = Paint().apply {
        color = resources.getColor(R.color.app_theme)
        strokeWidth = LINE_STROKE_WIDTH
        style = Paint.Style.STROKE
    }
    private val gradientPaint: Paint = Paint()
    private val linePath: Path = Path()
    private var expenses: List<Int>? = null

    init {
        gradientPaint.shader = LinearGradient(
            0f,
            0f,
            0f,
            height.toFloat(),
            Color.WHITE,
            resources.getColor(R.color.app_theme_transparent),
            Shader.TileMode.CLAMP
        )
    }

    fun setData(expensesData: ArrayList<String>) {
        expenses = expensesData.map { it.toInt() }
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val expensesList = expenses
        if (expensesList == null || expensesList.size < 2) {
            return
        }

        val width = width
        val height = height
        val xInterval = (width - 2 * GRAPH_PADDING).toFloat() / (expensesList.size - 1)
        val yScale = (height - 2 * GRAPH_PADDING).toFloat() / MAX_EXPENSE

        linePath.reset()
        linePath.moveTo(GRAPH_PADDING.toFloat(), (height - GRAPH_PADDING).toFloat())

        for (i in expensesList.indices) {
            val x = GRAPH_PADDING.toFloat() + i * xInterval
            val y = (height - GRAPH_PADDING).toFloat() - expensesList[i] * yScale
            linePath.lineTo(x, y)
        }

        canvas.drawPath(linePath, linePaint)
        val gradientPath = Path(linePath)
        val bounds = RectF()
        linePath.computeBounds(bounds, true)
        gradientPath.lineTo(bounds.right, height.toFloat())
        gradientPath.lineTo(bounds.left, height.toFloat())

        gradientPath.close()

        canvas.drawPath(gradientPath, gradientPaint)
    }
}