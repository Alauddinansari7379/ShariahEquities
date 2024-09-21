package com.amtech.shariahEquities.fragments
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.github.mikephil.charting.charts.PieChart
class GaugeChartWithPointer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : PieChart(context, attrs) {

    private val percentagePaint = Paint().apply {
        color = Color.BLACK
        textSize = 64f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private var percentage: Float = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the percentage in the middle of the chart
        drawPercentage(canvas)
    }

    // Set the percentage and redraw the chart
    fun setPercentage(value: Float) {
        this.percentage = value
        invalidate()
    }

    private fun drawPercentage(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f
        val percentageText = String.format("%.2f%%", percentage)
        canvas.drawText(percentageText, centerX, centerY, percentagePaint)
    }
}
