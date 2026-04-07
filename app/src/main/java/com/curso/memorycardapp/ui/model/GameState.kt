package com.curso.memorycardapp.ui.model


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.memorycardapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class GameState(config: GameConfiguration) : ViewModel() {
    private val _cards = mutableStateOf<List<CardData>>(emptyList())
    val cards: List<CardData> by _cards

    var errorCount by mutableIntStateOf(0)
    var matchedPairs by mutableIntStateOf(0)
    var isClickEnabled by mutableStateOf(true)
    val gridColumns by mutableStateOf(calculateGridColumns(config.numCardTypes))

    private var selectedIndices = mutableListOf<Int>()

    init {
        resetGame(config)
    }

    private fun calculateGridColumns(numCardTypes: Int): Int {
        return when {
            numCardTypes <= 6 -> 4  // 4x4 grid (16 cartas máximo)
            else -> 5               // 5x4 grid (20 cartas máximo)
        }
    }

    fun resetGame(config: GameConfiguration) {
        errorCount = 0
        matchedPairs = 0
        selectedIndices.clear()
        isClickEnabled = true

        // Asegurarnos que tenemos entre 4 y 10 tipos de cartas
        val numPairs = min(max(config.numCardTypes, 4), 10)
        val imageResources = listOf(
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
        ).take(numPairs)

        // Crear pares de cartas y calcular gridSize
        val pairs = (imageResources + imageResources).shuffled()
        _cards.value = pairs.mapIndexed { index, res ->
            CardData(index, res)
        }
    }

    fun flipCard(index: Int): Boolean {
        if (!isClickEnabled || _cards.value[index].isFaceUp || _cards.value[index].isMatched) {
            return false
        }

        _cards.value = _cards.value.toMutableList().apply {
            this[index] = this[index].copy(isFaceUp = true)
        }
        selectedIndices.add(index)

        if (selectedIndices.size == 2) {
            isClickEnabled = false
            val (first, second) = selectedIndices
            if (_cards.value[first].imageRes == _cards.value[second].imageRes) {
                _cards.value = _cards.value.toMutableList().apply {
                    this[first] = this[first].copy(isMatched = true)
                    this[second] = this[second].copy(isMatched = true)
                }
                matchedPairs++
                selectedIndices.clear()
                isClickEnabled = true
            } else {
                errorCount++
                // Voltear después de 1 segundo
                viewModelScope.launch {
                    delay(1000)
                    _cards.value = _cards.value.toMutableList().apply {
                        this[first] = this[first].copy(isFaceUp = false)
                        this[second] = this[second].copy(isFaceUp = false)
                    }
                    selectedIndices.clear()
                    isClickEnabled = true
                }
            }
        }
        return isClickEnabled
    }

    fun isGameComplete(): Boolean {
        return matchedPairs == _cards.value.size / 2
    }
}