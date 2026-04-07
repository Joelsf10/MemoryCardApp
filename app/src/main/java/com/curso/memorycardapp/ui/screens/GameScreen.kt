package com.curso.memorycardapp.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.curso.memorycardapp.ui.model.GameConfiguration
import com.curso.memorycardapp.ui.model.GameResult
import com.curso.memorycardapp.ui.model.GameState
import com.curso.memorycardapp.ui.model.GameStateFactory
import com.curso.memorycardapp.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    config: GameConfiguration,
    onGameEnd: (GameResult) -> Unit,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val gameState: GameState = viewModel(factory = GameStateFactory(config))
    var elapsedTime by remember { mutableStateOf(0) }
    var showResetDialog by remember { mutableStateOf(false) }
    var isTimerRunning by remember { mutableStateOf(true) }

    // Temporizador
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (true) {
                delay(1000L)
                if (!gameState.isGameComplete()) {
                    elapsedTime++
                } else {
                    isTimerRunning = false
                    onGameEnd(
                        GameResult(
                            playerName = config.playerName,
                            numCardTypes = config.numCardTypes,
                            timeSeconds = elapsedTime,
                            errorCount = gameState.errorCount,
                            isWinner = true
                        )
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        config.playerName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Text("←", style = MaterialTheme.typography.headlineSmall)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.primary
                ),
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            text = "${elapsedTime}s",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "Errores: ${gameState.errorCount}",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center // Centramos todo el contenido
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(), // Solo ocupa el alto necesario
                horizontalAlignment = Alignment.CenterHorizontally, // Centrado horizontal
                verticalArrangement = Arrangement.Center // Centrado vertical
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gameState.gridColumns),
                    modifier = Modifier
                        .padding(16.dp)
                        .widthIn(max = 500.dp) // Ancho máximo para no expandirse demasiado
                ) {
                    itemsIndexed(gameState.cards) { index, card ->
                        Card(
                            modifier = Modifier
                                .aspectRatio(0.75f)
                                .padding(4.dp) // Espacio entre cartas
                                .clickable(enabled = gameState.isClickEnabled) {
                                    gameState.flipCard(index)
                                },
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Parte IMPORTANTE: Mantenemos exactamente la misma lógica de visualización de cartas
                                if (card.isFaceUp || card.isMatched) {
                                    Image(
                                        painter = painterResource(card.imageRes),
                                        contentDescription = "Carta ${index + 1}",
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(R.drawable.back), // Asegúrate de tener este recurso
                                        contentDescription = "Dorso de carta",
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                if (card.isMatched) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.3f))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                shape = RoundedCornerShape(16.dp),
                title = {
                    Text("¿Salir de la partida?")
                },
                text = {
                    Text("Perderás tu progreso actual")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showResetDialog = false
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.errorContainer,
                            contentColor = colorScheme.onErrorContainer
                        )
                    ) {
                        Text("Salir")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showResetDialog = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}