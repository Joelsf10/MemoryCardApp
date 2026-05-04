package com.curso.memorycardapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.curso.memorycardapp.ui.model.GameConfiguration
import com.curso.memorycardapp.ui.model.PreferencesViewModel
import com.curso.memorycardapp.ui.model.PreferencesViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val prefsViewModel: PreferencesViewModel = viewModel(
        factory = PreferencesViewModelFactory(context)
    )
    val savedPrefs by prefsViewModel.preferences.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estado local del formulario, inicializado con los valores guardados
    var alias by remember(savedPrefs.playerName) { mutableStateOf(savedPrefs.playerName) }
    var numPares by remember(savedPrefs.numCardTypes) { mutableStateOf(savedPrefs.numCardTypes) }
    var timeEnabled by remember(savedPrefs.timeLimit) { mutableStateOf(savedPrefs.timeLimit != null) }
    var timeText by remember(savedPrefs.timeLimit) {
        mutableStateOf((savedPrefs.timeLimit ?: 60).toString())
    }

    val gridLabel = if (numPares <= 6) "4×4 (16 cartas)" else "5×4 (20 cartas)"
    val timeLimitValue = timeText.toIntOrNull()?.takeIf { it > 0 }
    val canSave = alias.isNotBlank() && (!timeEnabled || timeLimitValue != null)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Preferencias",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text(
                            "←",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = colorScheme.onPrimary
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Estas preferencias se usarán como valores por defecto en cada partida.",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )

            // Alias
            OutlinedTextField(
                value = alias,
                onValueChange = { alias = it },
                label = { Text("Alias del jugador") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            // Número de pares
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Pares distintos: $numPares", style = MaterialTheme.typography.titleMedium)
                    Slider(
                        value = numPares.toFloat(),
                        onValueChange = { numPares = it.toInt() },
                        valueRange = GameConfiguration.MIN_CARD_TYPES.toFloat()..GameConfiguration.MAX_CARD_TYPES.toFloat(),
                        steps = 6,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        (GameConfiguration.MIN_CARD_TYPES..GameConfiguration.MAX_CARD_TYPES).forEach { v ->
                            Text(
                                text = "$v",
                                color = if (numPares == v) colorScheme.primary
                                else colorScheme.onSurface.copy(alpha = 0.4f),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.clickable { numPares = v }
                            )
                        }
                    }
                    Text(
                        text = "Tablero: $gridLabel",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.primary
                    )
                }
            }

            // Control de tiempo
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = timeEnabled, onCheckedChange = { timeEnabled = it })
                        Spacer(Modifier.width(8.dp))
                        Text("Control de tiempo", style = MaterialTheme.typography.titleMedium)
                    }
                    if (timeEnabled) {
                        OutlinedTextField(
                            value = timeText,
                            onValueChange = {
                                if (it.length <= 4 && it.all(Char::isDigit)) timeText = it
                            },
                            label = { Text("Tiempo máximo (segundos)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            isError = timeLimitValue == null,
                            supportingText = {
                                if (timeLimitValue == null)
                                    Text("Introduce un valor mayor que 0")
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        prefsViewModel.guardar(
                            GameConfiguration(
                                playerName = alias,
                                numCardTypes = numPares,
                                timeLimit = if (timeEnabled) timeLimitValue else null
                            )
                        )
                        onBack()
                    },
                    enabled = canSave,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}