package com.curso.memorycardapp.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.rememberCoroutineScope
import com.curso.memorycardapp.data.db.MemoryCardDatabase
import com.curso.memorycardapp.data.repository.Repository
import com.curso.memorycardapp.ui.model.GameEndReason
import kotlinx.coroutines.launch
import com.curso.memorycardapp.ui.model.GameResult
import com.curso.memorycardapp.ui.utils.showToast

private const val DEFAULT_EMAIL = ""

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    result: GameResult,
    onRestart: () -> Unit,
    onExit: () -> Unit,
    onQuit: () -> Unit,
    onPreferences: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Guardar la partida en Room automáticamente al entrar a esta pantalla
    LaunchedEffect(Unit) {
        scope.launch {
            val db = MemoryCardDatabase.getInstance(context)
            val repo = Repository(db.partidaDao())
            repo.guardarPartida(result)
        }
    }

    var editableEmail by rememberSaveable { mutableStateOf(DEFAULT_EMAIL) }

    val isEmailValid = editableEmail.isNotBlank() &&
            Patterns.EMAIL_ADDRESS.matcher(editableEmail).matches()

    val titleText = when {
        result.isWinner -> "Has ganado"
        result.endReason == GameEndReason.TIME_UP -> "Tiempo agotado"
        else -> "Has perdido"
    }
    val topBarColor = when {
        result.isWinner -> Color(0xFF3B6D11)
        result.endReason == GameEndReason.TIME_UP -> Color(0xFF854F0B)
        else -> colorScheme.error
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                actions = {
                    androidx.compose.material3.IconButton(onClick = onPreferences) {
                        Text("⚙️", fontSize = 22.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarColor)
            )
        }
    ) { scaffoldPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .background(colorScheme.background)
        ) {
            val isLandscape = maxWidth > maxHeight

            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ResultTitle(result)
                        ResultDataSection(
                            result = result,
                            email = editableEmail,
                            onEmailChange = { editableEmail = it }
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(4.dp))
                        StatsSummaryCard(result, colorScheme)
                        ActionButtons(
                            isEmailValid = isEmailValid,
                            onSend = { sendEmail(context, editableEmail, result.finishedAt, result.toLogText()) },
                            onRestart = onRestart,
                            onExit = onExit,
                            onQuit = onQuit
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ResultTitle(result)
                    StatsSummaryCard(result, colorScheme)
                    ResultDataSection(
                        result = result,
                        email = editableEmail,
                        onEmailChange = { editableEmail = it }
                    )
                    ActionButtons(
                        isEmailValid = isEmailValid,
                        onSend = { sendEmail(context, editableEmail, result.finishedAt, result.toLogText()) },
                        onRestart = onRestart,
                        onExit = onExit,
                        onQuit = onQuit
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultTitle(result: GameResult) {
    val colorScheme = MaterialTheme.colorScheme
    val (emoji, _) = when {
        result.isWinner -> "🎉" to Color(0xFF2E7D32)
        result.endReason == GameEndReason.TIME_UP -> "⏱" to Color(0xFFE65100)
        else -> "💀" to colorScheme.error
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(emoji, style = MaterialTheme.typography.headlineMedium)
        result.timeRemaining?.let { rem ->
            if (result.isWinner && rem > 0)
                Text(
                    "Te han sobrado ${rem}s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF3B6D11)
                )
        }
    }
}


@Composable
private fun ResultDataSection(
    result: GameResult,
    email: String,
    onEmailChange: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    // Foco en el campo email al entrar a la pantalla
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant,
            contentColor = colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReadOnlyField(label = "Día y hora", value = result.finishedAt)
            ReadOnlyField(label = "Log de la partida", value = result.toLogText(), multiline = true)

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email destinatario") },
                placeholder = { Text("ejemplo@correo.com") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                isError = email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                supportingText = {
                    if (email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches())
                        Text("Email no válido")
                }
            )
        }
    }
}

@Composable
private fun ReadOnlyField(label: String, value: String, multiline: Boolean = false) {
    val colorScheme = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = if (multiline)
                MaterialTheme.typography.bodySmall
            else
                MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun StatsSummaryCard(result: GameResult, colorScheme: androidx.compose.material3.ColorScheme) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant,
            contentColor = colorScheme.onSurfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            StatRow("Jugador", result.playerName)
            Divider(modifier = Modifier.padding(vertical = 6.dp))
            StatRow("Pares", "${result.numCardTypes}")
            Divider(modifier = Modifier.padding(vertical = 6.dp))
            StatRow("Tiempo", formatTime(result.timeSeconds))
            Divider(modifier = Modifier.padding(vertical = 6.dp))
            StatRow("Errores", "${result.errorCount}")
            result.timeRemaining?.let {
                Divider(modifier = Modifier.padding(vertical = 6.dp))
                StatRow("Tiempo sobrante", "${it}s")
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ActionButtons(
    isEmailValid: Boolean,
    onSend: () -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit,
    onQuit: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Button(
        onClick = onSend,
        enabled = isEmailValid,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) { Text("📧 Enviar por email") }

    Button(
        onClick = onRestart,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) { Text("🔄 Jugar otra vez") }

    OutlinedButton(
        onClick = onExit,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) { Text("🏠 Menú principal") }

    OutlinedButton(
        onClick = onQuit,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.error),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.error)
    ) { Text("Salir de la app") }
}

private fun sendEmail(context: Context, email: String, dateTime: String, log: String) {
    try {
        val subject = "Log – $dateTime"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, log)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(intent, "Enviar log via:"))
        } else {
            showToast(context, "No hay cliente de email instalado")
        }
    } catch (e: Exception) {
        showToast(context, "Error: ${e.localizedMessage}")
    }
}

private fun formatTime(s: Int) = "%02d:%02d".format(s / 60, s % 60)