package com.curso.memorycardapp.ui.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "partidas")
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val alias: String,
    val fechaHora: String,          // Día y hora de finalización
    val numPares: Int,              // Nº de pares distintos jugados
    val tiempoSegundos: Int,        // Tiempo total empleado en segundos
    val errores: Int,               // Nº de errores cometidos
    val resultado: String           // "Victoria", "Derrota", "Tiempo agotado"
)