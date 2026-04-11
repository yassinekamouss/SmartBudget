package com.yassinekamouss.smartbudget.domain.repository

import com.yassinekamouss.smartbudget.domain.model.Category
import com.yassinekamouss.smartbudget.domain.model.Expense
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

enum class DeleteStrategy {
    RESTRICT,
    MOVE_TO_OTHER
}

interface BudgetRepository {
    fun getExpensesBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>>
    fun getTotalExpensesBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<Double?>
    suspend fun insertExpense(expense: Expense)
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)

    fun getAllCategories(): Flow<List<Category>>
    suspend fun insertCategory(category: Category)
    
    @Throws(IllegalStateException::class)
    suspend fun deleteCategory(categoryId: Long, strategy: DeleteStrategy)
}
