package com.curso.memorycardapp.ui.model

import java.io.Serializable

data class GameConfiguration(
    val playerName: String = "",
    val numCardTypes: Int = DEFAULT_NUM_CARD_TYPES,
    val timeLimit: Int? = null
) : Serializable {
    companion object {
        const val DEFAULT_NUM_CARD_TYPES = 4
        const val MIN_CARD_TYPES = 4
        const val MAX_CARD_TYPES = 10
    }
}