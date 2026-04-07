package com.curso.memorycardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.curso.memorycardapp.ui.screens.MemoryCardNavigation
import com.curso.memorycardapp.ui.theme.MemoryCardAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemoryCardAppTheme {
                MemoryCardNavigation()
            }
        }
    }
}