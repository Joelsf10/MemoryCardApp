package com.curso.memorycardapp.ui.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class GameEndReason { WON, TIME_UP }

data class GameResult(
    val playerName: String,
    val numCardTypes: Int,
    val timeSeconds: Int,
    val errorCount: Int,
    val isWinner: Boolean,
    val endReason: GameEndReason = if (isWinner) GameEndReason.WON else GameEndReason.TIME_UP,
    val timeLimitSeconds: Int? = null,           // null = sin límite
    val finishedAt: String = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
) {
    val timeRemaining: Int?
        get() = timeLimitSeconds?.let { it - timeSeconds }

    fun toLogText(): String {
        val sb = StringBuilder()
        sb.appendLine("Alias: $playerName")
        sb.appendLine("Pares distintos: $numCardTypes")
        sb.appendLine("Tiempo empleado: ${formatTime(timeSeconds)}")
        sb.appendLine("Errores: $errorCount")
        when {
            isWinner -> {
                sb.appendLine("Has ganado !!")
                timeRemaining?.let { sb.appendLine("Te han sobrado $it segundos !") }
            }
            endReason == GameEndReason.TIME_UP -> sb.appendLine("Has agotado el tiempo !!")
            else -> sb.appendLine("Has perdido !!")
        }
        return sb.toString().trimEnd()
    }

    private fun formatTime(s: Int) = "%02d:%02d".format(s / 60, s % 60)
}