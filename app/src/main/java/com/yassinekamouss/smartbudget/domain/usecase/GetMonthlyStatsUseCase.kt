package com.yassinekamouss.smartbudget.domain.usecase
import timber.log.Timber

import com.yassinekamouss.smartbudget.domain.model.CategorySummary
import com.yassinekamouss.smartbudget.domain.model.MonthlyStats
import com.yassinekamouss.smartbudget.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.YearMonth
import javax.inject.Inject

class GetMonthlyStatsUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    operator fun invoke(year: Int, month: Int): Flow<MonthlyStats> {
        val targetMonth = YearMonth.of(year, month)
        val startDate = targetMonth.atDay(1)
        val endDate = targetMonth.atEndOfMonth()

        // Combine categories and expenses flows reactively
        return combine(
            repository.getAllCategories(),
            repository.getExpensesBetweenDates(startDate, endDate)
        ) { categories, expenses ->

            Timber.d("Month %s-%s: %d expenses found.", year, month, expenses.size)

            // Calculate total safely using BigDecimal
            val totalAmountBD = expenses.fold(BigDecimal.ZERO) { acc, expense ->
                acc.add(BigDecimal.valueOf(expense.amount))
            }
            val totalAmount = totalAmountBD.toDouble()

            val categoriesMap = categories.associateBy { it.id }

            // Group expenses securely and build summaries
            val summaries = expenses
                .groupBy { it.categoryId }
                .map { (categoryId, catExpenses) ->
                    val catTotalBD = catExpenses.fold(BigDecimal.ZERO) { acc, exp ->
                        acc.add(BigDecimal.valueOf(exp.amount))
                    }
                    val catTotal = catTotalBD.toDouble()

                    // Secure percentage calculation
                    val percentageBD = if (totalAmountBD > BigDecimal.ZERO) {
                        catTotalBD.divide(totalAmountBD, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal(100))
                    } else {
                        BigDecimal.ZERO
                    }
                    val percentage = percentageBD.setScale(1, RoundingMode.HALF_UP).toFloat()

                    val category = categoriesMap[categoryId]
                    CategorySummary(
                        categoryId = categoryId,
                        name = category?.name ?: "Catégorie inconnue",
                        color = category?.color ?: 0xFF9E9E9E.toInt(), // Default gray
                        total = catTotal,
                        percentage = percentage
                    )
                }
                .sortedByDescending { it.total }

            val topCategories = summaries.take(3)

            MonthlyStats(
                totalAmount = totalAmount,
                categorySummaries = summaries,
                topCategories = topCategories
            )
        }
    }
}
