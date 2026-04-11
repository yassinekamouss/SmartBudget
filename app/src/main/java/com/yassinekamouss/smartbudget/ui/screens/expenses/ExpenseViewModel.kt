package com.yassinekamouss.smartbudget.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yassinekamouss.smartbudget.domain.model.Category
import com.yassinekamouss.smartbudget.domain.model.Expense
import com.yassinekamouss.smartbudget.domain.model.MonthlyStats
import com.yassinekamouss.smartbudget.domain.repository.BudgetRepository
import com.yassinekamouss.smartbudget.domain.usecase.AddExpenseUseCase
import com.yassinekamouss.smartbudget.domain.usecase.GetMonthlyStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlin.random.Random

data class ExpenseUiState(
    val monthlyStats: MonthlyStats? = null,
    val expenses: List<Expense> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedMonth: YearMonth = YearMonth.now()
)

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val getMonthlyStatsUseCase: GetMonthlyStatsUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val repository: BudgetRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<ExpenseUiState> = _selectedMonth
        .flatMapLatest { month ->
            val startDate = month.atDay(1)
            val endDate = month.atEndOfMonth()
            
            combine(
                getMonthlyStatsUseCase(month.year, month.monthValue).catch { e -> 
                    timber.log.Timber.e(e, "Error in getMonthlyStatsUseCase")
                    emit(MonthlyStats(0.0, emptyList(), emptyList())) 
                },
                repository.getExpensesBetweenDates(startDate, endDate).catch { e -> 
                    timber.log.Timber.e(e, "Error in getExpensesBetweenDates")
                    emit(emptyList()) 
                },
                repository.getAllCategories().catch { e -> 
                    timber.log.Timber.e(e, "Error in getAllCategories")
                    emit(emptyList()) 
                }
            ) { stats, expenses, categories ->
                ExpenseUiState(
                    monthlyStats = stats,
                    expenses = expenses,
                    categories = categories,
                    isLoading = false,
                    error = null,
                    selectedMonth = month
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ExpenseUiState(isLoading = true, selectedMonth = _selectedMonth.value)
        )

    fun changeMonth(year: Int, month: Int) {
        _selectedMonth.value = YearMonth.of(year, month)
    }

    fun nextMonth() {
        _selectedMonth.value = _selectedMonth.value.plusMonths(1)
    }

    fun previousMonth() {
        _selectedMonth.value = _selectedMonth.value.minusMonths(1)
    }

    fun addExpense(
        amount: Double,
        categoryId: Long,
        date: LocalDate,
        note: String?,
        paymentMethod: String? = null
    ) {
        viewModelScope.launch {
            addExpenseUseCase(amount, categoryId, date, note, paymentMethod)
        }
    }
    
    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }
    
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun exportCurrentMonthCsv(context: android.content.Context, exportCsvUseCase: com.yassinekamouss.smartbudget.domain.usecase.ExportCsvUseCase): Boolean {
        val expenses = uiState.value.expenses
        if (expenses.isEmpty()) {
            timber.log.Timber.w("Export cancelled: No expenses to export for ${uiState.value.selectedMonth}")
            return false
        }
        val monthYear = "${uiState.value.selectedMonth.monthValue}_${uiState.value.selectedMonth.year}"
        val uri = exportCsvUseCase(expenses, monthYear)
        
        if (uri != null) {
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                putExtra(android.content.Intent.EXTRA_SUBJECT, "Export SmartBudget - $monthYear")
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            timber.log.Timber.d("Starting Chooser Intent")
            context.startActivity(android.content.Intent.createChooser(intent, "Share CSV file via"))
            return true
        }
        return false
    }

    fun seedDemoData() {
        viewModelScope.launch {
            try {
                val categories = repository.getAllCategories().first()
                if (categories.isEmpty()) return@launch // Ensure categories exist

                val today = LocalDate.now()
                val currentMonth = YearMonth.from(today)
                val previousMonth = currentMonth.minusMonths(1)

                val notes = listOf("Supermarket", "Bus ticket", "Coffee", "Cinema", "Pharmacy", "Books", "Gym", "Rent", "Dinner", "Gas")

                // Generate 30 random expenses across 2 months
                for (i in 1..30) {
                    val targetMonth = if (i % 2 == 0) currentMonth else previousMonth
                    val randomDay = Random.nextInt(1, targetMonth.lengthOfMonth() + 1)
                    val randomDate = targetMonth.atDay(randomDay)
                    
                    val randomAmount = Random.nextDouble(20.0, 500.0)
                    val randomCategory = categories.random()
                    val randomNote = notes.random()

                    // Only skip check constraint on 0
                    if (randomAmount > 0) {
                       repository.insertExpense(
                           Expense(
                               amount = Math.round(randomAmount * 100) / 100.0,
                               categoryId = randomCategory.id,
                               date = randomDate,
                               note = randomNote
                           )
                       )
                    }
                }
            } catch (e: Exception) {
                // Handle or log error
            }
        }
    }
}
