package com.curso.memorycardapp.ui.components


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorCounter(
    count: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Errores: $count",
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier.padding(8.dp)
    )
}