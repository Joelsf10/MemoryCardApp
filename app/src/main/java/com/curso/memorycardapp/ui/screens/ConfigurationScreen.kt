package com.curso.memorycardapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.curso.memorycardapp.ui.model.GameConfiguration

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    onStart: (GameConfiguration) -> Unit,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var playerName by remember { mutableStateOf("") }
    var numCardTypes by remember { mutableStateOf(GameConfiguration.DEFAULT_NUM_CARD_TYPES) }
    var timeLimitEnabled by remember { mutableStateOf(false) }
    var timeLimitText by remember { mutableStateOf("60") }

    val gridLabel = if (numCardTypes <= 6) "4×4 (16 cartas)" else "5×4 (20 cartas)"
    val timeLimitValue = timeLimitText.toIntOrNull()?.takeIf { it > 0 }
    val canStart = playerName.isNotBlank() && (!timeLimitEnabled || timeLimitValue != null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Configuración",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onPrimary
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primary
                )
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val isLandscape = maxWidth > maxHeight

            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PlayerNameField(playerName) { playerName = it }
                        PairsSelector(numCardTypes, colorScheme, gridLabel) { numCardTypes = it }
                    }
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Spacer(Modifier.height(4.dp))
                        TimeLimitSection(timeLimitEnabled, timeLimitText,
                            onToggle = { timeLimitEnabled = it },
                            onTextChange = { timeLimitText = it })
                        Spacer(Modifier.height(8.dp))
                        ActionButtons(canStart, onBack) {
                            onStart(GameConfiguration(
                                playerName = playerName,
                                numCardTypes = numCardTypes,
                                timeLimit = if (timeLimitEnabled) timeLimitValue else null
                            ))
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PlayerNameField(playerName) { playerName = it }
                    PairsSelector(numCardTypes, colorScheme, gridLabel) { numCardTypes = it }
                    TimeLimitSection(timeLimitEnabled, timeLimitText,
                        onToggle = { timeLimitEnabled = it },
                        onTextChange = { timeLimitText = it })
                    Spacer(Modifier.height(4.dp))
                    ActionButtons(canStart, onBack) {
                        onStart(GameConfiguration(
                            playerName = playerName,
                            numCardTypes = numCardTypes,
                            timeLimit = if (timeLimitEnabled) timeLimitValue else null
                        ))
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerNameField(value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text("Tu nombre (alias)") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
}

@Composable
private fun PairsSelector(
    numCardTypes: Int,
    colorScheme: androidx.compose.material3.ColorScheme,
    gridLabel: String,
    onChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Pares distintos: $numCardTypes", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = numCardTypes.toFloat(),
                onValueChange = { onChange(it.toInt()) },
                valueRange = GameConfiguration.MIN_CARD_TYPES.toFloat()..GameConfiguration.MAX_CARD_TYPES.toFloat(),
                steps = 6,
                modifier = Modifier.fillMaxWidth()
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                (GameConfiguration.MIN_CARD_TYPES..GameConfiguration.MAX_CARD_TYPES).forEach { v ->
                    Text(
                        text = "$v",
                        color = if (numCardTypes == v) colorScheme.primary
                        else colorScheme.onSurface.copy(alpha = 0.4f),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.clickable { onChange(v) }
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
}

@Composable
private fun TimeLimitSection(
    enabled: Boolean,
    timeLimitText: String,
    onToggle: (Boolean) -> Unit,
    onTextChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = enabled, onCheckedChange = onToggle)
                Spacer(Modifier.width(8.dp))
                Text("Control de tiempo", style = MaterialTheme.typography.titleMedium)
            }
            if (enabled) {
                OutlinedTextField(
                    value = timeLimitText,
                    onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) onTextChange(it) },
                    label = { Text("Tiempo máximo (segundos)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    isError = timeLimitText.toIntOrNull()?.let { it <= 0 } ?: true,
                    supportingText = {
                        if (timeLimitText.toIntOrNull()?.let { it <= 0 } != false)
                            Text("Introduce un valor mayor que 0")
                    }
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(canStart: Boolean, onBack: () -> Unit, onStart: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Atrás") }
        Button(onClick = onStart, enabled = canStart, modifier = Modifier.weight(1f)) { Text("Comenzar") }
    }
}