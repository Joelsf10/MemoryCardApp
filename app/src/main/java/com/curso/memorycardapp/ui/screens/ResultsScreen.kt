package com.curso.memorycardapp.ui.screens


import android.content.Context
import android.content.Intent
import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.curso.memorycardapp.ui.model.GameResult
import com.curso.memorycardapp.ui.utils.showToast

@Composable
fun ResultsScreen(
    result: GameResult,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var email by remember { mutableStateOf("") }
    var showEmailDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = if (result.isWinner) "¡Has ganado!" else "Juego terminado",
                style = MaterialTheme.typography.displaySmall.copy(
                    color = colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )

            // Tarjeta de resultados
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surfaceVariant,
                    contentColor = colorScheme.onSurfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    ResultItem("Jugador", result.playerName)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    ResultItem("Pares distintos", result.numCardTypes.toString())
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    ResultItem("Tiempo", formatTime(result.timeSeconds))
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    ResultItem("Errores", result.errorCount.toString())
                }
            }

            // Botón de email
            OutlinedButton(
                onClick = { showEmailDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Enviar resultados por email")
            }

            // Botones principales
            Button(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Jugar otra vez")
            }

            OutlinedButton(
                onClick = onExit,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Menú principal")
            }
        }
    }

    // Diálogo de email
    if (showEmailDialog) {
        AlertDialog(
            onDismissRequest = { showEmailDialog = false },
            shape = RoundedCornerShape(16.dp),
            title = {
                Text("Enviar resultados")
            },
            text = {
                Column {
                    Text("Ingresa el email de destino:")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        label = { Text("Email") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (sendEmail(context, email, result)) {
                            showEmailDialog = false
                        }
                    },
                    enabled = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showEmailDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}


private fun sendEmail(context: Context, email: String, result: GameResult): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "Resultados del Memory Card - ${result.playerName}")
            putExtra(Intent.EXTRA_TEXT, createEmailBody(result))
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(
                Intent.createChooser(intent, "Enviar resultados via:")
            )
            true
        } else {
            showToast(context, "No hay aplicaciones de email instaladas")
            false
        }
    } catch (e: Exception) {
        showToast(context, "Error al enviar email: ${e.localizedMessage}")
        false
    }
}


private fun createEmailBody(result: GameResult): String {
    return """
        Resultados del Memory Card:
        
        Jugador: ${result.playerName}
        Pares distintos: ${result.numCardTypes}
        Tiempo: ${result.timeSeconds} segundos
        Errores: ${result.errorCount}
        Resultado: ${if (result.isWinner) "Ganador" else "Perdedor"}
        
        ¡Gracias por jugar!
    """.trimIndent()
}

@Composable
private fun ResultItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}