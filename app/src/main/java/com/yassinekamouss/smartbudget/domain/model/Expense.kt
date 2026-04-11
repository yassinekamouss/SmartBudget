package com.yassinekamouss.smartbudget.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Expense(
    val id: Long = 0,
    val amount: Double,
    val currency: String = "MAD",
    val date: LocalDate,
    val categoryId: Long,
    val note: String? = null,
    val paymentMethod: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(amount > 0) { "Le montant de la dépense doit être strictement positif." }
    }
}
