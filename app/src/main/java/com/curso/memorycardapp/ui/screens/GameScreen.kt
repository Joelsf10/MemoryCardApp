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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.curso.memorycardapp.ui.utils.isTablet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    config: GameConfiguration,
    windowSizeClass: WindowSizeClass,
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

    val isTablet = windowSizeClass.isTablet()

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
    val timerText = if (uiState.hasTimeLimit) "⏱ ${uiState.timeRemaining ?: 0}s"
    else formatElapsed(uiState.elapsedSeconds)

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
                        Text("←", style = MaterialTheme.typography.headlineSmall.copy(
                            color = colorScheme.onPrimary
                        ))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.primary),
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
        if (isTablet) {
            // TABLET: bi-panel (cuadrícula | log)
            TabletGameLayout(
                uiState = uiState,
                modifier = Modifier.fillMaxSize().padding(padding),
                onCardClick = { gameViewModel.flipCard(it) }
            )
        } else {
            // SMARTPHONE: mono-panel (solo cuadrícula)
            SmartphoneGameLayout(
                uiState = uiState,
                modifier = Modifier.fillMaxSize().padding(padding),
                onCardClick = { gameViewModel.flipCard(it) }
            )
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
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
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

// Layout de tablet: cuadrícula a la izquierda, log a la derecha
@Composable
private fun TabletGameLayout(
    uiState: com.curso.memorycardapp.ui.model.GameUiState,
    modifier: Modifier,
    onCardClick: (Int) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val logListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto-scroll al final cuando hay nuevas entradas de log
    LaunchedEffect(uiState.logLines.size) {
        if (uiState.logLines.isNotEmpty()) {
            scope.launch {
                logListState.animateScrollToItem(uiState.logLines.size - 1)
            }
        }
    }

    Row(modifier = modifier) {
        // Panel principal: cuadrícula del juego
        Box(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxHeight()
                .background(colorScheme.surfaceVariant)
                .padding(8.dp)
        ) {
            CardGrid(
                uiState = uiState,
                columns = uiState.gridColumns,
                cardAspect = 0.75f,
                onCardClick = onCardClick
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = colorScheme.outline.copy(alpha = 0.3f)
        )

        // Panel secundario: log en tiempo real
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colorScheme.surface)
                .padding(12.dp)
        ) {
            Text(
                text = "LOG…",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                state = logListState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.logLines) { line ->
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurface
                    )
                    Divider(color = colorScheme.outline.copy(alpha = 0.2f))
                }
            }
        }
    }
}

// Layout de smartphone
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun SmartphoneGameLayout(
    uiState: com.curso.memorycardapp.ui.model.GameUiState,
    modifier: Modifier,
    onCardClick: (Int) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    BoxWithConstraints(
        modifier = modifier.background(colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        val isLandscape = maxWidth > maxHeight
        val columns = if (isLandscape) uiState.gridColumns + 2 else uiState.gridColumns
        val cardAspect = if (isLandscape) 0.65f else 0.75f
        val gridPadding = if (isLandscape) 4.dp else 10.dp

        CardGrid(
            uiState = uiState,
            columns = columns,
            cardAspect = cardAspect,
            gridPadding = gridPadding,
            fillMax = isLandscape,
            onCardClick = onCardClick
        )
    }
}

// Cuadrícula de cartas reutilizable
@Composable
private fun CardGrid(
    uiState: com.curso.memorycardapp.ui.model.GameUiState,
    columns: Int,
    cardAspect: Float,
    gridPadding: androidx.compose.ui.unit.Dp = 10.dp,
    fillMax: Boolean = true,
    onCardClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = if (fillMax) Modifier.fillMaxSize().padding(gridPadding)
        else Modifier.fillMaxWidth().padding(gridPadding),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(uiState.cards) { index, card ->
            FlippableCard(
                card = card,
                aspectRatio = cardAspect,
                onClick = { onCardClick(index) },
                enabled = uiState.isClickEnabled
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
            .graphicsLayer { rotationY = rotation; cameraDistance = 12f * density }
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
                    modifier = Modifier.fillMaxSize().graphicsLayer { rotationY = 180f }
                )
                if (card.isMatched) {
                    Box(modifier = Modifier.fillMaxSize().background(Color(0x5500C853)))
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