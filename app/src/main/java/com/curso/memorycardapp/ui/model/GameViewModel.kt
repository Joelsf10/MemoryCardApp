package com.curso.memorycardapp.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.memorycardapp.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

sealed class GameEvent {
    data object CardAlreadyFlipped : GameEvent()
    data object TimeLimitReached : GameEvent()
}

data class GameUiState(
    val cards: List<CardData> = emptyList(),
    val errorCount: Int = 0,
    val matchedPairs: Int = 0,
    val elapsedSeconds: Int = 0,
    val isClickEnabled: Boolean = true,
    val isGameComplete: Boolean = false,
    val gridColumns: Int = 4,
    val totalPairs: Int = 0,
    val timeLimitSeconds: Int? = null   // null = sin límite de tiempo
) {
    val timeRemaining: Int?
        get() = timeLimitSeconds?.let { max(0, it - elapsedSeconds) }

    val hasTimeLimit: Boolean
        get() = timeLimitSeconds != null
}

class GameViewModel(private val config: GameConfiguration) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<GameEvent?>(null)
    val events: StateFlow<GameEvent?> = _events.asStateFlow()

    private var selectedIndices = mutableListOf<Int>()
    private var timerJob: Job? = null

    private val imageResources = listOf(
        R.drawable.water,
        R.drawable.lightning,
        R.drawable.fire,
        R.drawable.grass,
        R.drawable.darkness,
        R.drawable.doubles,
        R.drawable.fairy,
        R.drawable.fighting,
        R.drawable.metal,
        R.drawable.psychic
    )

    init {
        resetGame()
    }

    private fun calculateGridColumns(numCardTypes: Int): Int =
        if (numCardTypes <= 6) 4 else 5

    fun resetGame() {
        timerJob?.cancel()
        selectedIndices.clear()

        val numPairs = min(max(config.numCardTypes, GameConfiguration.MIN_CARD_TYPES), GameConfiguration.MAX_CARD_TYPES)
        val columns = calculateGridColumns(numPairs)
        val selected = imageResources.take(numPairs)
        val pairs = (selected + selected).shuffled()

        _uiState.update {
            GameUiState(
                cards = pairs.mapIndexed { idx, res -> CardData(idx, res) },
                gridColumns = columns,
                totalPairs = numPairs,
                timeLimitSeconds = config.timeLimit,
                isClickEnabled = true
            )
        }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val state = _uiState.value
                if (state.isGameComplete) break

                val newElapsed = state.elapsedSeconds + 1
                _uiState.update { it.copy(elapsedSeconds = newElapsed) }

                val limit = config.timeLimit
                if (limit != null && newElapsed >= limit) {
                    _events.value = GameEvent.TimeLimitReached
                    break
                }
            }
        }
    }

    fun consumeEvent() { _events.value = null }

    fun flipCard(index: Int) {
        val state = _uiState.value
        if (!state.isClickEnabled) return

        val card = state.cards[index]
        if (card.isFaceUp || card.isMatched) {
            _events.value = GameEvent.CardAlreadyFlipped
            return
        }

        val newCards = state.cards.toMutableList().apply {
            this[index] = this[index].copy(isFaceUp = true)
        }
        _uiState.update { it.copy(cards = newCards) }
        selectedIndices.add(index)

        if (selectedIndices.size == 2) {
            _uiState.update { it.copy(isClickEnabled = false) }
            val (first, second) = selectedIndices
            if (newCards[first].imageRes == newCards[second].imageRes) {
                val matched = newCards.toMutableList().apply {
                    this[first] = this[first].copy(isMatched = true)
                    this[second] = this[second].copy(isMatched = true)
                }
                val newMatched = state.matchedPairs + 1
                val complete = newMatched == state.totalPairs
                _uiState.update {
                    it.copy(cards = matched, matchedPairs = newMatched, isClickEnabled = true, isGameComplete = complete)
                }
                if (complete) timerJob?.cancel()
                selectedIndices.clear()
            } else {
                viewModelScope.launch {
                    delay(1000L)
                    val flipped = _uiState.value.cards.toMutableList().apply {
                        this[first] = this[first].copy(isFaceUp = false)
                        this[second] = this[second].copy(isFaceUp = false)
                    }
                    _uiState.update { it.copy(cards = flipped, errorCount = it.errorCount + 1, isClickEnabled = true) }
                    selectedIndices.clear()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}