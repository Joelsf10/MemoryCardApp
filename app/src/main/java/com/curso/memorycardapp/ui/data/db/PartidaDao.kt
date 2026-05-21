package com.curso.memorycardapp.ui.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidaDao {

    // Inserta una nueva partida. Si hay conflicto de ID, la reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(partida: PartidaEntity)

    // Devuelve todas las partidas ordenadas de más reciente a más antigua.
    @Query("SELECT * FROM partidas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<PartidaEntity>>

    // Devuelve una partida concreta por su ID.
    @Query("SELECT * FROM partidas WHERE id = :id")
    suspend fun obtenerPorId(id: Int): PartidaEntity?

    // Elimina todas las partidas (útil para tests o reset).
    @Query("DELETE FROM partidas")
    suspend fun eliminarTodas()
}
