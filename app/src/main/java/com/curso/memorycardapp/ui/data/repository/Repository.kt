package com.curso.memorycardapp.ui.data.repository

import com.curso.memorycardapp.ui.data.db.PartidaDao
import com.curso.memorycardapp.ui.data.db.PartidaEntity
import com.curso.memorycardapp.ui.model.GameEndReason
import com.curso.memorycardapp.ui.model.GameResult
import kotlinx.coroutines.flow.Flow


class Repository(private val dao: PartidaDao) {

    //Flow con todas las partidas. La UI se actualiza automáticamente
    val todasLasPartidas: Flow<List<PartidaEntity>> = dao.obtenerTodas()

    // Guarda un GameResult como PartidaEntity en la base de datos.
    suspend fun guardarPartida(result: GameResult) {
        val resultado = when {
            result.isWinner -> "Victoria"
            result.endReason == GameEndReason.TIME_UP -> "Tiempo agotado"
            else -> "Derrota"
        }
        dao.insertar(
            PartidaEntity(
                alias = result.playerName,
                fechaHora = result.finishedAt,
                numPares = result.numCardTypes,
                tiempoSegundos = result.timeSeconds,
                errores = result.errorCount,
                resultado = resultado
            )
        )
    }

    // Obtiene una partida concreta por ID para ver su detalle.
    suspend fun obtenerDetalle(id: Int): PartidaEntity? = dao.obtenerPorId(id)
}