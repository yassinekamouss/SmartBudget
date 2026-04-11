package com.yassinekamouss.smartbudget.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yassinekamouss.smartbudget.data.local.converter.DateTimeConverter
import com.yassinekamouss.smartbudget.data.local.dao.CategoryDao
import com.yassinekamouss.smartbudget.data.local.dao.ExpenseDao
import com.yassinekamouss.smartbudget.data.local.entity.CategoryEntity
import com.yassinekamouss.smartbudget.data.local.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeConverter::class)
abstract class SmartBudgetDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
}
