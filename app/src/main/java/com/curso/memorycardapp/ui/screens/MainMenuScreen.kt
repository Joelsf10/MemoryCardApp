package com.curso.memorycardapp.ui.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuScreen(
    onPlay: () -> Unit,
    onHelp: () -> Unit,
    onExit: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val buttonShape = RoundedCornerShape(12.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Memory Card",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = colorScheme.primary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Botón de Nueva Partida
            Button(
                onClick = onPlay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                shape = buttonShape,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Text(
                    "Nueva Partida",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Cómo Jugar
            OutlinedButton(
                onClick = onHelp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorScheme.primary
                ),
                border = BorderStroke(2.dp, colorScheme.primary),
                shape = buttonShape
            ) {
                Text(
                    "Cómo Jugar",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nuevo Botón para Salir
            OutlinedButton(
                onClick = onExit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorScheme.error
                ),
                border = BorderStroke(2.dp, colorScheme.error),
                shape = buttonShape
            ) {
                Text(
                    "Salir",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            // Decoración opcional (usando color secundario)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 48.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorScheme.secondary.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}