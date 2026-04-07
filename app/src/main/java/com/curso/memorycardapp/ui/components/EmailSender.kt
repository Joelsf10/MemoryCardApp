package com.curso.memorycardapp.ui.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.curso.memorycardapp.ui.model.LogData

@Composable
fun EmailSender(
    logData: LogData,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Resultados:", style = MaterialTheme.typography.bodyMedium)
        Text("Tiempo: ${logData.timeSpent}s")
        Text("Pares: ${logData.matches}")
        Text("Errores: ${logData.errors}")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSend,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar por Email")
        }
    }
}