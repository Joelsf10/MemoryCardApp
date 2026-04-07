package com.curso.memorycardapp.ui.model

data class GameConfiguration(
    val playerName: String = "",
    val numCardTypes: Int = 4,  // Número de tipos de cartas distintas (4-10)
    val timeLimit: Int? = null  // Opcional: límite de tiempo en segundos
)