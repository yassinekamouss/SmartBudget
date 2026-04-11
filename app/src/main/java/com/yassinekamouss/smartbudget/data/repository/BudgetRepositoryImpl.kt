package com.yassinekamouss.smartbudget.data.repository

import com.yassinekamouss.smartbudget.data.local.dao.CategoryDao
import com.yassinekamouss.smartbudget.data.local.dao.ExpenseDao
import com.yassinekamouss.smartbudget.data.mapper.toDomain
import com.yassinekamouss.smartbudget.data.mapper.toEntity
import com.yassinekamouss.smartbudget.domain.model.Category
import com.yassinekamouss.smartbudget.domain.model.Expense
import com.yassinekamouss.smartbudget.domain.repository.BudgetRepository
import com.yassinekamouss.smartbudget.domain.repository.DeleteStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao
) : BudgetRepository {

    override fun getExpensesBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>> {
        return expenseDao.getExpensesBetweenDates(startDate, endDate)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTotalExpensesBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<Double?> {
        return expenseDao.getTotalExpensesBetweenDates(startDate, endDate)
    }

    override suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense.toEntity())
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense.toEntity())
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun deleteCategory(categoryId: Long, strategy: DeleteStrategy) {
        val expenseCount = expenseDao.getExpenseCountByCategoryId(categoryId)

        if (expenseCount > 0) {
            when (strategy) {
                DeleteStrategy.RESTRICT -> {
                    throw IllegalStateException("Impossible de supprimer la catégorie car elle contient des dépenses. Veuillez transférer les dépenses d'abord.")
                }
                DeleteStrategy.MOVE_TO_OTHER -> {
                    val defaultCategory = categoryDao.getCategoryByName("Autre")
                        ?: throw IllegalStateException("La catégorie de repli 'Autre' est introuvable.")
                    
                    expenseDao.moveExpensesToCategory(categoryId, defaultCategory.id)
                    categoryDao.deleteCategoryById(categoryId)
                }
            }
        } else {
            categoryDao.deleteCategoryById(categoryId)
        }
    }
}
