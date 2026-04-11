package com.yassinekamouss.smartbudget.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.yassinekamouss.smartbudget.domain.model.CategorySummary

@Composable
fun SimplePieChart(
    summaries: List<CategorySummary>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val strokeWidth = size.minDimension * 0.15f
        var startAngle = -90f

        summaries.forEach { summary ->
            val sweepAngle = summary.percentage * 3.6f // 360 degrees / 100
            if (sweepAngle > 0) {
                drawArc(
                    color = Color(summary.color),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
                startAngle += sweepAngle
            }
        }
        
        // Emphasize an empty state if no summaries? Not needed, handled by UI
        if (summaries.isEmpty()) {
            drawArc(
                color = Color.LightGray.copy(alpha = 0.3f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )
        }
    }
}
