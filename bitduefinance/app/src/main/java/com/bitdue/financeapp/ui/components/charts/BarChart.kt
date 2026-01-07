package com.bitdue.financeapp.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bitdue.financeapp.ui.theme.NegativeRed
import com.bitdue.financeapp.ui.theme.PositiveGreen

data class BarChartData(
    val label: String,
    val income: Float,
    val expense: Float
)

@Composable
fun IncomeExpenseBarChart(
    data: List<BarChartData>,
    modifier: Modifier = Modifier,
    incomeColor: Color = PositiveGreen,
    expenseColor: Color = NegativeRed
) {
    val maxValue = remember(data) {
        data.maxOfOrNull { maxOf(it.income, it.expense) } ?: 0f
    }
    
    if (maxValue == 0f || data.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = "No data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }
    
    Column(modifier = modifier) {
        // Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barWidth = (size.width / data.size) * 0.35f
                val groupWidth = size.width / data.size
                val chartHeight = size.height - 30.dp.toPx()
                
                data.forEachIndexed { index, item ->
                    val groupStart = index * groupWidth
                    val centerX = groupStart + groupWidth / 2
                    
                    // Income bar
                    val incomeHeight = (item.income / maxValue) * chartHeight
                    drawRoundRect(
                        color = incomeColor,
                        topLeft = Offset(centerX - barWidth - 2.dp.toPx(), chartHeight - incomeHeight),
                        size = Size(barWidth, incomeHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                    
                    // Expense bar
                    val expenseHeight = (item.expense / maxValue) * chartHeight
                    drawRoundRect(
                        color = expenseColor,
                        topLeft = Offset(centerX + 2.dp.toPx(), chartHeight - expenseHeight),
                        size = Size(barWidth, expenseHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }
        }
        
        // Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { item ->
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier.size(12.dp)) {
                drawCircle(color = incomeColor)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Income",
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.width(24.dp))
            
            Canvas(modifier = Modifier.size(12.dp)) {
                drawCircle(color = expenseColor)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Expense",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
