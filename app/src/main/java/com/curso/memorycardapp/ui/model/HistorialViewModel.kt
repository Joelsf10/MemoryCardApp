package com.curso.memorycardapp.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.curso.memorycardapp.ui.data.repository.Repository
import com.curso.memorycardapp.ui.data.db.PartidaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn


class HistorialViewModel(private val repository: Repository) : ViewModel() {

    // Lista de todas las partidas, actualizada automáticamente desde Room.
    val partidas: StateFlow<List<PartidaEntity>> = repository.todasLasPartidas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Partida seleccionada para ver su detalle (panel secundario en tablets).
    private val _partidaSeleccionada = MutableStateFlow<PartidaEntity?>(null)
    val partidaSeleccionada: StateFlow<PartidaEntity?> = _partidaSeleccionada.asStateFlow()

    fun seleccionarPartida(partida: PartidaEntity) {
        _partidaSeleccionada.value = partida
    }

    fun limpiarSeleccion() {
        _partidaSeleccionada.value = null
    }
}

class HistorialViewModelFactory(
    private val repository: Repository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistorialViewModel::class.java)) {
            return HistorialViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}