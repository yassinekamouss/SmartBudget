package com.yassinekamouss.smartbudget.domain.usecase

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.yassinekamouss.smartbudget.domain.model.Expense
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

class ExportCsvUseCase @Inject constructor(
    private val context: Context
) {
    operator fun invoke(expenses: List<Expense>, monthYear: String): Uri? {
        return try {
            timber.log.Timber.d("Starting CSV export for $monthYear with ${expenses.size} expenses")
            val fileName = "export_smartbudget_$monthYear.csv"
            val file = File(context.cacheDir, fileName)
            val writer = FileWriter(file)
            writer.append("Date,Category,Amount,Note\n")
            expenses.forEach {
                writer.append("${it.date},${it.categoryId},${String.format(java.util.Locale.US, "%.2f", it.amount)},${it.note ?: ""}\n")
            }
            writer.flush()
            writer.close()
            
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            timber.log.Timber.d("CSV export successful. URI: $uri")
            uri
        } catch (e: Exception) {
            timber.log.Timber.e(e, "Failed to export CSV")
            null
        }
    }
}
