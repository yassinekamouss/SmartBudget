package com.yassinekamouss.smartbudget.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yassinekamouss.smartbudget.domain.model.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

class DatabaseCallback(
    private val databaseProvider: Provider<SmartBudgetDatabase>
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Populate default categories on the very first database creation
        CoroutineScope(Dispatchers.IO).launch {
            val dao = databaseProvider.get().categoryDao()
            val entities = Category.defaultCategories.map {
                com.yassinekamouss.smartbudget.data.local.entity.CategoryEntity(
                    name = it.name,
                    icon = it.icon,
                    color = it.color,
                    isActive = it.isActive
                )
            }
            dao.insertCategories(entities)
        }
    }
}
