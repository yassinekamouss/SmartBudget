package com.yassinekamouss.smartbudget.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Int,
    val isActive: Boolean = true
) {
    companion object {
        val defaultCategories = listOf(
            Category(name = "Alimentation", icon = "\uD83C\uDF7D\uFE0F", color = 0xFF4CAF50.toInt()),
            Category(name = "Transport", icon = "\uD83D\uDE8C", color = 0xFF2196F3.toInt()),
            Category(name = "Logement", icon = "\uD83C\uDFE0", color = 0xFFFF9800.toInt()),
            Category(name = "Santé", icon = "\uD83C\uDFE5", color = 0xFFF44336.toInt()),
            Category(name = "Loisirs", icon = "\uD83C\uDFAC", color = 0xFF9C27B0.toInt()),
            Category(name = "Études", icon = "\uD83D\uDCDA", color = 0xFFFFEB3B.toInt()),
            Category(name = "Autre", icon = "\uD83D\uDCE6", color = 0xFF9E9E9E.toInt())
        )
    }
}
