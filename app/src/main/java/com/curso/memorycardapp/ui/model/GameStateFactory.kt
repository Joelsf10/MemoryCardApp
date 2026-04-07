package com.curso.memorycardapp.ui.model


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameStateFactory(private val config: GameConfiguration) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameState::class.java)) {
            return GameState(config) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}