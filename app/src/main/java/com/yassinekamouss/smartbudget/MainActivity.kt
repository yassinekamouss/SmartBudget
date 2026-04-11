package com.yassinekamouss.smartbudget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yassinekamouss.smartbudget.ui.screens.MainScreen
import com.yassinekamouss.smartbudget.ui.theme.SmartbudgetTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartbudgetTheme {
                MainScreen()
            }
        }
    }
}
