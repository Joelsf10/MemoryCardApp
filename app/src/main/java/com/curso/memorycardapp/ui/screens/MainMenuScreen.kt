package com.curso.memorycardapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curso.memorycardapp.R

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onPlay: () -> Unit,
    onHelp: () -> Unit,
    onHistory: () -> Unit,
    onPreferences: () -> Unit,
    onExit: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val buttonShape = RoundedCornerShape(14.dp)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    IconButton(onClick = onPreferences) {
                        Text(text = "⚙️", fontSize = 22.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        // Fix 3.1: BoxWithConstraints para detectar landscape y ajustar layout
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorScheme.primaryContainer,
                            colorScheme.background
                        )
                    )
                )
        ) {
            val isLandscape = maxWidth > maxHeight

            if (isLandscape) {
                // En landscape: logo + título a la izquierda, botones a la derecha
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AppLogo()
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = stringResource(R.string.app_subtitle),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    // Botones en columna con scroll por si la pantalla es muy pequeña
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = onPlay,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary
                            ),
                            shape = buttonShape
                        ) { Text(stringResource(R.string.menu_new_game), style = MaterialTheme.typography.titleMedium) }

                        OutlinedButton(
                            onClick = onHistory,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.primary),
                            border = BorderStroke(1.5.dp, colorScheme.primary),
                            shape = buttonShape
                        ) { Text(stringResource(R.string.menu_history), style = MaterialTheme.typography.titleMedium) }

                        OutlinedButton(
                            onClick = onHelp,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.primary),
                            border = BorderStroke(1.5.dp, colorScheme.primary),
                            shape = buttonShape
                        ) { Text(stringResource(R.string.menu_help), style = MaterialTheme.typography.titleMedium) }

                        OutlinedButton(
                            onClick = onExit,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.error),
                            border = BorderStroke(1.5.dp, colorScheme.error),
                            shape = buttonShape
                        ) { Text(stringResource(R.string.menu_exit), style = MaterialTheme.typography.titleMedium) }
                    }
                }
            } else {
                // Portrait: columna centrada con scroll
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppLogo()
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.displaySmall.copy(
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = stringResource(R.string.app_subtitle),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(bottom = 48.dp, top = 6.dp)
                    )

                    Button(
                        onClick = onPlay,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        shape = buttonShape,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) { Text(stringResource(R.string.menu_new_game), style = MaterialTheme.typography.titleMedium) }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onHistory,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.primary),
                        border = BorderStroke(1.5.dp, colorScheme.primary),
                        shape = buttonShape
                    ) { Text(stringResource(R.string.menu_history), style = MaterialTheme.typography.titleMedium) }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onHelp,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.primary),
                        border = BorderStroke(1.5.dp, colorScheme.primary),
                        shape = buttonShape
                    ) { Text(stringResource(R.string.menu_help), style = MaterialTheme.typography.titleMedium) }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onExit,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.error),
                        border = BorderStroke(1.5.dp, colorScheme.error),
                        shape = buttonShape
                    ) { Text(stringResource(R.string.menu_exit), style = MaterialTheme.typography.titleMedium) }
                }
            }
        }
    }
}

@Composable
private fun AppLogo() {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CardTile(filled = true)
                CardTile(filled = false)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CardTile(filled = false)
                CardTile(filled = true)
            }
        }
    }
}

@Composable
private fun CardTile(filled: Boolean) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(width = 22.dp, height = 30.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (filled) colorScheme.onPrimary.copy(alpha = 0.9f)
                else colorScheme.onPrimary.copy(alpha = 0.35f)
            )
    ) {
        if (filled) {
            Text(
                text = "?",
                color = colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}