package com.curso.memorycardapp.ui.model


data class LogData(
    val alias: String,          // Nombre del jugador
    val gridSize: Int,          // Tamaño de parrilla (4, 6, 8)
    val timeSpent: Int,         // Tiempo en segundos
    val matches: Int,           // Pares encontrados
    val errors: Int,            // Errores cometidos
    val isWinner: Boolean       // ¿Ganó la partida?
) {
    fun toEmailBody(): String {
        return """
            Alias: $alias
            Parrilla: ${gridSize}x$gridSize
            Tiempo: $timeSpent segundos
            Pares: $matches
            Errores: $errors
            Resultado: ${if (isWinner) "GANADOR" else "PERDEDOR"}
        """.trimIndent()
    }
}