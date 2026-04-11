package com.yassinekamouss.smartbudget.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Expenses : Screen("expenses", "Expenses", Icons.Default.List)
    object Stats : Screen("stats", "Stats", Icons.Default.Info)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}
