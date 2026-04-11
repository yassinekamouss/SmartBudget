package com.yassinekamouss.smartbudget.data.mapper

import com.yassinekamouss.smartbudget.data.local.entity.CategoryEntity
import com.yassinekamouss.smartbudget.data.local.entity.ExpenseEntity
import com.yassinekamouss.smartbudget.domain.model.Category
import com.yassinekamouss.smartbudget.domain.model.Expense

fun CategoryEntity.toDomain() = Category(id, name, icon, color, isActive)
fun Category.toEntity() = CategoryEntity(id, name, icon, color, isActive)

fun ExpenseEntity.toDomain() = Expense(id, amount, currency, date, categoryId, note, paymentMethod, createdAt, updatedAt)
fun Expense.toEntity() = ExpenseEntity(id, amount, currency, date, categoryId, note, paymentMethod, createdAt, updatedAt)
