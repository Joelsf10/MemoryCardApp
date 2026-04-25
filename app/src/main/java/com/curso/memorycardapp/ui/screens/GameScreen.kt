package com.curso.memorycardapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.curso.memorycardapp.R
import com.curso.memorycardapp.ui.model.CardData
import com.curso.memorycardapp.ui.model.GameConfiguration
import com.curso.memorycardapp.ui.model.GameEndReason
import com.curso.memorycardapp.ui.model.GameEvent
import com.curso.memorycardapp.ui.model.GameResult
import com.curso.memorycardapp.ui.model.GameViewModel
import com.curso.memorycardapp.ui.model.GameViewModelFactory

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    config: GameConfiguration,
    onGameEnd: (GameResult) -> Unit,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(config))
    val uiState by gameViewModel.uiState.collectAsState()
    val event by gameViewModel.events.collectAsState()

    var showExitDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    var gameEndFired by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(event) {
        when (event) {
            is GameEvent.CardAlreadyFlipped -> {
                snackbarHostState.showSnackbar("⚠️ Esta carta ya está destapada")
                gameViewModel.consumeEvent()
            }
            is GameEvent.TimeLimitReached -> {
                if (!gameEndFired) {
                    gameEndFired = true
                    onGameEnd(GameResult(
                        playerName = config.playerName,
                        numCardTypes = config.numCardTypes,
                        timeSeconds = uiState.elapsedSeconds,
                        errorCount = uiState.errorCount,
                        isWinner = false,
                        endReason = GameEndReason.TIME_UP,
                        timeLimitSeconds = config.timeLimit
                    ))
                }
                gameViewModel.consumeEvent()
            }
            null -> Unit
        }
    }

    LaunchedEffect(uiState.isGameComplete) {
        if (uiState.isGameComplete && !gameEndFired) {
            gameEndFired = true
            onGameEnd(GameResult(
                playerName = config.playerName,
                numCardTypes = config.numCardTypes,
                timeSeconds = uiState.elapsedSeconds,
                errorCount = uiState.errorCount,
                isWinner = true,
                endReason = GameEndReason.WON,
                timeLimitSeconds = config.timeLimit
            ))
        }
    }

    val timerColor = if (uiState.hasTimeLimit) Color(0xFFFFCC02) else colorScheme.onPrimary
    val timerText = if (uiState.hasTimeLimit) {
        "⏱ ${uiState.timeRemaining ?: 0}s"
    } else {
        formatElapsed(uiState.elapsedSeconds)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = config.playerName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onPrimary
                            )
                        )
                        Text(
                            text = "Pares: ${uiState.matchedPairs}/${uiState.totalPairs}  ·  Errores: ${uiState.errorCount}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colorScheme.onPrimary.copy(alpha = 0.75f)
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Text(
                            text = "←",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = colorScheme.onPrimary
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primary
                ),
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(
                            text = timerText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = timerColor
                            )
                        )
                    }
                }
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colorScheme.surfaceVariant)
        ) {
            val isLandscape = maxWidth > maxHeight
            val columns = if (isLandscape) uiState.gridColumns + 2 else uiState.gridColumns
            val gridPadding = if (isLandscape) 4.dp else 10.dp
            val cardPadding = if (isLandscape) 2.dp else 4.dp
            val cardAspect = if (isLandscape) 0.65f else 0.75f

            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize().padding(gridPadding),
                verticalArrangement = Arrangement.spacedBy(cardPadding),
                horizontalArrangement = Arrangement.spacedBy(cardPadding)
            ) {
                itemsIndexed(uiState.cards) { index, card ->
                    FlippableCard(
                        card = card,
                        aspectRatio = cardAspect,
                        onClick = { gameViewModel.flipCard(index) },
                        enabled = uiState.isClickEnabled
                    )
                }
            }
        }

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                shape = RoundedCornerShape(16.dp),
                title = { Text("¿Salir de la partida?") },
                text = { Text("Perderás el progreso actual.") },
                confirmButton = {
                    Button(
                        onClick = { showExitDialog = false; onBack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.errorContainer,
                            contentColor = colorScheme.onErrorContainer
                        )
                    ) { Text("Salir") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showExitDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
private fun FlippableCard(
    card: CardData,
    aspectRatio: Float,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFaceUp || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "card_flip"
    )

    Card(
        modifier = Modifier
            .aspectRatio(aspectRatio)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (rotation > 90f) {
                Image(
                    painter = painterResource(card.imageRes),
                    contentDescription = "Carta",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                )
                if (card.isMatched) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x5500C853))
                    )
                }
            } else {
                Image(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "Dorso",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private fun formatElapsed(seconds: Int): String =
    "%02d:%02d".format(seconds / 60, seconds % 60)