package com.curso.memorycardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.curso.memorycardapp.ui.screens.MemoryCardNavigation
import com.curso.memorycardapp.ui.theme.MemoryCardAppTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoryCardAppTheme {
                val windowSizeClass = calculateWindowSizeClass(this)
                MemoryCardNavigation(windowSizeClass = windowSizeClass)
            }
        }
    }
}