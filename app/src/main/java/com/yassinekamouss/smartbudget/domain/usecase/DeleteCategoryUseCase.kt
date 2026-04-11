package com.yassinekamouss.smartbudget.domain.usecase

import com.yassinekamouss.smartbudget.domain.repository.BudgetRepository
import com.yassinekamouss.smartbudget.domain.repository.DeleteStrategy
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(categoryId: Long, strategy: DeleteStrategy): Result<Unit> {
        return try {
            repository.deleteCategory(categoryId, strategy)
            Result.success(Unit)
        } catch (e: IllegalStateException) {
            // Capte l'erreur levée par la stratégie RESTRICT du repository
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(Exception("Erreur inattendue lors de la suppression de la catégorie.", e))
        }
    }
}
