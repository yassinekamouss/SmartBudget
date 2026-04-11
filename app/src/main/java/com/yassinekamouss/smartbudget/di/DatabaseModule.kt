package com.yassinekamouss.smartbudget.di

import android.content.Context
import androidx.room.Room
import com.yassinekamouss.smartbudget.data.local.DatabaseCallback
import com.yassinekamouss.smartbudget.data.local.SmartBudgetDatabase
import com.yassinekamouss.smartbudget.data.local.dao.CategoryDao
import com.yassinekamouss.smartbudget.data.local.dao.ExpenseDao
import com.yassinekamouss.smartbudget.data.repository.BudgetRepositoryImpl
import com.yassinekamouss.smartbudget.domain.repository.BudgetRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        databaseProvider: Provider<SmartBudgetDatabase>
    ): SmartBudgetDatabase {
        return Room.databaseBuilder(
            context,
            SmartBudgetDatabase::class.java,
            "smartbudget_db"
        )
        .addCallback(DatabaseCallback(databaseProvider))
        .build()
    }

    @Provides
    @Singleton
    fun provideExpenseDao(database: SmartBudgetDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: SmartBudgetDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideBudgetRepository(
        expenseDao: ExpenseDao,
        categoryDao: CategoryDao
    ): BudgetRepository {
        return BudgetRepositoryImpl(expenseDao, categoryDao)
    }
}
