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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.curso.memorycardapp.R
import com.curso.memorycardapp.ui.model.GameConfiguration
import com.curso.memorycardapp.ui.model.PreferencesViewModel
import com.curso.memorycardapp.ui.model.PreferencesViewModelFactory


@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    initialConfig: GameConfiguration = GameConfiguration(),
    onStart: (GameConfiguration) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    // Usar el ViewModel compartido — fix 2.11
    val prefsViewModel: PreferencesViewModel = viewModel(
        factory = PreferencesViewModelFactory(context)
    )

    // Inicializar el formulario con la config recibida (solo la primera vez)
    LaunchedEffect(initialConfig) {
        prefsViewModel.initFormIfNeeded(initialConfig)
    }

    // Observar el estado del formulario desde el ViewModel
    val playerName     by prefsViewModel.editAlias.collectAsState()
    val numCardTypes   by prefsViewModel.editNumPares.collectAsState()
    val timeLimitEnabled by prefsViewModel.editTimeEnabled.collectAsState()
    val timeLimitText  by prefsViewModel.editTimeText.collectAsState()

    val gridLabel = if (numCardTypes <= 6)
        stringResource(R.string.grid_label_small)
    else
        stringResource(R.string.grid_label_large)

    val timeLimitValue = timeLimitText.toIntOrNull()?.takeIf { it > 0 }
    val canStart = playerName.isNotBlank() && (!timeLimitEnabled || timeLimitValue != null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.screen_config_title),
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
                        PlayerNameField(playerName) { prefsViewModel.onAliasChange(it) }
                        PairsSelector(numCardTypes, colorScheme, gridLabel) {
                            prefsViewModel.onNumParesChange(it)
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Spacer(Modifier.height(4.dp))
                        TimeLimitSection(
                            enabled      = timeLimitEnabled,
                            timeLimitText = timeLimitText,
                            onToggle     = { prefsViewModel.onTimeEnabledChange(it) },
                            onTextChange = { prefsViewModel.onTimeTextChange(it) }
                        )
                        Spacer(Modifier.height(8.dp))
                        ActionButtons(canStart, onBack) {
                            val config = prefsViewModel.buildCurrentConfig()
                            onStart(config)
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
                    PlayerNameField(playerName) { prefsViewModel.onAliasChange(it) }
                    PairsSelector(numCardTypes, colorScheme, gridLabel) {
                        prefsViewModel.onNumParesChange(it)
                    }
                    TimeLimitSection(
                        enabled      = timeLimitEnabled,
                        timeLimitText = timeLimitText,
                        onToggle     = { prefsViewModel.onTimeEnabledChange(it) },
                        onTextChange = { prefsViewModel.onTimeTextChange(it) }
                    )
                    Spacer(Modifier.height(4.dp))
                    ActionButtons(canStart, onBack) {
                        val config = prefsViewModel.buildCurrentConfig()
                        onStart(config)
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
        label = { Text(stringResource(R.string.label_alias)) },
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
            Text(
                stringResource(R.string.label_pairs, numCardTypes),
                style = MaterialTheme.typography.titleMedium
            )
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
                text = stringResource(R.string.label_grid, gridLabel),
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = enabled, onCheckedChange = onToggle)
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.label_time_control),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            if (enabled) {
                OutlinedTextField(
                    value = timeLimitText,
                    onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) onTextChange(it) },
                    label = { Text(stringResource(R.string.label_time_max)) },
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
                            Text(stringResource(R.string.error_time_value))
                    }
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(canStart: Boolean, onBack: () -> Unit, onStart: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.btn_back))
        }
        Button(onClick = onStart, enabled = canStart, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.btn_start))
        }
    }
}