package com.curso.memorycardapp.ui.components


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GameTimer(
    isActive: Boolean,
    timeLeft: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Tiempo: ${timeLeft}s",
        style = MaterialTheme.typography.bodySmall,
        color = if (isActive) Color.Red else Color.Blue,
        modifier = modifier.padding(8.dp)
    )
}