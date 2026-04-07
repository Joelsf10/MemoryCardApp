package com.curso.memorycardapp.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.curso.memorycardapp.ui.model.GameConfiguration

@Composable
fun ConfigurationScreen(
    onStart: (GameConfiguration) -> Unit,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var playerName by remember { mutableStateOf("") }
    var numCardTypes by remember { mutableStateOf(4) }
    val gridColumns = remember(numCardTypes) {
        when {
            numCardTypes <= 6 -> "4x4 (16 cartas)"
            else -> "5x4 (20 cartas)"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Título
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = colorScheme.primary,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Campo de nombre simplificado
        OutlinedTextField(
            value = playerName,
            onValueChange = { playerName = it },
            label = { Text("Tu nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        // Selector de pares
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Número de pares distintos: $numCardTypes",
                style = MaterialTheme.typography.titleMedium
            )

            Slider(
                value = numCardTypes.toFloat(),
                onValueChange = { numCardTypes = it.toInt() },
                valueRange = 4f..10f,
                steps = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Indicador visual de los valores posibles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (4..10).forEach { value ->
                    Text(
                        text = "$value",
                        color = if (numCardTypes == value) colorScheme.primary
                        else colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.clickable { numCardTypes = value }
                    )
                }
            }
        }

        // Info tamaño del tablero
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Tamaño del tablero: $gridColumns",
                modifier = Modifier.padding(16.dp)
            )
        }

        // Botones inferiores
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Atrás")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    onStart(
                        GameConfiguration(
                            playerName = playerName,
                            numCardTypes = numCardTypes
                        )
                    )
                },
                enabled = playerName.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Comenzar")
            }
        }
    }
}