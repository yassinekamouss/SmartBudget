package com.yassinekamouss.smartbudget.domain.model

data class CategorySummary(
    val categoryId: Long,
    val name: String,
    val color: Int,
    val total: Double,
    val percentage: Float,
    val limitAmount: Double? = null
)

data class MonthlyStats(
    val totalAmount: Double,
    val categorySummaries: List<CategorySummary>,
    val topCategories: List<CategorySummary>
)
