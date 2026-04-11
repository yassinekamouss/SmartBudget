package com.yassinekamouss.smartbudget.domain.usecase

import com.yassinekamouss.smartbudget.domain.model.Expense
import com.yassinekamouss.smartbudget.domain.repository.BudgetRepository
import java.time.LocalDate
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(
        amount: Double,
        categoryId: Long,
        date: LocalDate,
        note: String? = null,
        paymentMethod: String? = null
    ): Result<Unit> {
        return try {
            require(amount > 0) { "Le montant doit être strictement positif." }
            // Additional date validation if needed (e.g. not too far in the future/past)
            require(date.year > 2000) { "La date semble invalide." }

            val expense = Expense(
                amount = amount,
                categoryId = categoryId,
                date = date,
                note = note,
                paymentMethod = paymentMethod
            )
            repository.insertExpense(expense)
            Result.success(Unit)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(Exception("Erreur lors de l'ajout de la dépense.", e))
        }
    }
}
