package com.bitdue.financeapp.ui.components.charts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bitdue.financeapp.ui.theme.NegativeRed
import com.bitdue.financeapp.ui.theme.PositiveGreen
import com.bitdue.financeapp.ui.theme.WarningOrange

@Composable
fun BudgetProgressBar(
    categoryName: String,
    categoryIcon: String,
    spent: Double,
    limit: Double,
    modifier: Modifier = Modifier
) {
    val percentage = if (limit > 0) (spent / limit).coerceIn(0.0, 1.0) else 0.0
    val remaining = (limit - spent).coerceAtLeast(0.0)
    
    val progressColor = when {
        percentage >= 1.0 -> NegativeRed
        percentage >= 0.8 -> WarningOrange
        else -> PositiveGreen
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Text(
                        text = categoryIcon,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "${String.format("%.0f", percentage * 100)}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = { percentage.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Spent: Ksh ${String.format("%.2f", spent)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Limit: Ksh ${String.format("%.2f", limit)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (remaining > 0 && percentage < 1.0) {
                Text(
                    text = "Ksh ${String.format("%.2f", remaining)} remaining",
                    style = MaterialTheme.typography.bodySmall,
                    color = PositiveGreen,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else if (percentage >= 1.0) {
                Text(
                    text = "Budget exceeded by Ksh ${String.format("%.2f", spent - limit)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NegativeRed,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
