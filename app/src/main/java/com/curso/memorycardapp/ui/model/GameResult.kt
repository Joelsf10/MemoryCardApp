package com.curso.memorycardapp.ui.model

data class GameResult(
    val playerName: String,
    val numCardTypes: Int,
    val timeSeconds: Int,
    val errorCount: Int,
    val isWinner: Boolean
)