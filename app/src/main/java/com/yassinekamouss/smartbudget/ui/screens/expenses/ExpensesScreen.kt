package com.yassinekamouss.smartbudget.ui.screens.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yassinekamouss.smartbudget.domain.model.Category
import com.yassinekamouss.smartbudget.domain.model.Expense
import com.yassinekamouss.smartbudget.ui.components.AddExpenseSheet
import com.yassinekamouss.smartbudget.ui.components.EmptyStateContent
import com.yassinekamouss.smartbudget.ui.components.ExpenseItem

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showAddSheet by remember { mutableStateOf(false) }
    var selectedExpenseToEdit by remember { mutableStateOf<Expense?>(null) }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedExpenseToEdit = null
                    showAddSheet = true
                },
                modifier = Modifier.padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Filter Bar
                if (uiState.categories.isNotEmpty() && uiState.expenses.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategoryId == null,
                                onClick = { selectedCategoryId = null },
                                label = { Text("All") }
                            )
                        }
                        items(uiState.categories) { category ->
                            FilterChip(
                                selected = selectedCategoryId == category.id,
                                onClick = { selectedCategoryId = category.id },
                                label = { Text(category.name) }
                            )
                        }
                    }
                }

                // List or Empty State
                val filteredExpenses = if (selectedCategoryId == null) {
                    uiState.expenses
                } else {
                    uiState.expenses.filter { it.categoryId == selectedCategoryId }
                }

                if (filteredExpenses.isEmpty()) {
                    EmptyStateContent(
                        icon = Icons.Default.List,
                        message = "No expenses to show.\\nTap + to add one."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
                    ) {
                        items(
                            items = filteredExpenses,
                            key = { it.id } // Ensures stable keys for swipe-to-dismiss
                        ) { expense ->
                            var showDeleteDialog by remember { mutableStateOf(false) }

                            if (showDeleteDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDeleteDialog = false },
                                    title = { Text("Supprimer la dépense") },
                                    text = { Text("Voulez-vous vraiment supprimer cette dépense ?") },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            viewModel.deleteExpense(expense)
                                            showDeleteDialog = false
                                            scope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "Dépense supprimée",
                                                    actionLabel = "Annuler",
                                                    duration = SnackbarDuration.Short
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    // Undo logic
                                                    viewModel.addExpense(
                                                        expense.amount,
                                                        expense.categoryId,
                                                        expense.date,
                                                        expense.note,
                                                        expense.paymentMethod
                                                    )
                                                }
                                            }
                                        }) {
                                            Text(
                                                "Supprimer",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showDeleteDialog = false }) {
                                            Text("Annuler")
                                        }
                                    }
                                )
                            }

                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->
                                    if (value == SwipeToDismissBoxValue.EndToStart) {
                                        showDeleteDialog = true
                                        false // Don't immediately dismiss, wait for dialog
                                    } else {
                                        false
                                    }
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = {
                                    val color =
                                        if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                            MaterialTheme.colorScheme.errorContainer
                                        } else {
                                            Color.Transparent
                                        }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                            .background(color, MaterialTheme.shapes.medium)
                                            .padding(16.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                },
                                enableDismissFromStartToEnd = false
                            ) {
                                val category =
                                    uiState.categories.find { it.id == expense.categoryId }
                                Box(modifier = Modifier.clickable {
                                    selectedExpenseToEdit = expense
                                    showAddSheet = true
                                }) {
                                    ExpenseItem(
                                        expense = expense,
                                        category = category
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddSheet) {
            AddExpenseSheet(
                categories = uiState.categories,
                expenseToEdit = selectedExpenseToEdit,
                onDismiss = {
                    showAddSheet = false
                    selectedExpenseToEdit = null
                },
                onSave = { amount, categoryId, date, note ->
                    if (selectedExpenseToEdit != null) {
                        val updatedExpense = selectedExpenseToEdit!!.copy(
                            amount = amount,
                            categoryId = categoryId,
                            date = date,
                            note = note,
                            paymentMethod = null
                        )
                        viewModel.updateExpense(updatedExpense)
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Dépense mise à jour",
                                actionLabel = "Annuler",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.updateExpense(selectedExpenseToEdit!!)
                            }
                        }
                    } else {
                        viewModel.addExpense(amount, categoryId, date, note, null)
                    }
                    showAddSheet = false
                    selectedExpenseToEdit = null
                }
            )
        }
    }
}