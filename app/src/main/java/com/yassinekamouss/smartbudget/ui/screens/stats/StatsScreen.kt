package com.yassinekamouss.smartbudget.ui.screens.stats

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yassinekamouss.smartbudget.domain.model.CategorySummary
import com.yassinekamouss.smartbudget.ui.components.EmptyStateContent
import com.yassinekamouss.smartbudget.ui.components.SimplePieChart
import com.yassinekamouss.smartbudget.ui.screens.expenses.ExpenseViewModel
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState // Pour nettoyer ton code

@Composable
fun StatsScreen(
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val monthlyStats = uiState.monthlyStats

    AnimatedContent(
        targetState = monthlyStats,
        transitionSpec = {
            fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
        },
        label = "StatsCrossfade"
    ) { stats ->
        if (stats == null || stats.totalAmount <= 0.0) {
            EmptyStateContent(
                icon = Icons.Default.Info,
                message = "Not enough data for statistics in ${uiState.selectedMonth.month.name.lowercase().replaceFirstChar { it.titlecase() }}."
            )
        } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(androidx.compose.foundation.rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Total Budget
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total Expenses",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = String.format("%.2f MAD", stats.totalAmount),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Pie Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SimplePieChart(
                            summaries = stats.categorySummaries,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Top Categories List
                    Text(
                        text = "Top Categories",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    stats.topCategories.forEach { summary ->
                        CategoryStatItem(summary = summary)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
        }
    }
}

@Composable
fun CategoryStatItem(summary: CategorySummary) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(summary.color))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = summary.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${String.format("%.1f", summary.percentage)}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = String.format("%.2f MAD", summary.total),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        summary.limitAmount?.let { limit ->
            val ratio = (summary.total / limit).toFloat().coerceIn(0f, 1f)
            val isOverBudget = ratio >= 0.9f
            
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { ratio },
                    modifier = Modifier.weight(1f).height(6.dp),
                    color = if (isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = androidx.compose.material3.ProgressIndicatorDefaults.CircularIndeterminateStrokeCap
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = String.format("%.0f/%.0f", summary.total, limit),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
