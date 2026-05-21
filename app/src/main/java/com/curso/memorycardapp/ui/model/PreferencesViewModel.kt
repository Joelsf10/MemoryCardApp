package com.curso.memorycardapp.ui.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.curso.memorycardapp.ui.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferencesViewModel(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    // Configuración guardada persistentemente en DataStore
    val preferences: StateFlow<GameConfiguration> = repository.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GameConfiguration()
        )

    // --- Estado editable del formulario (fix 2.11) ---
    // Separado de las preferencias guardadas para que sobreviva rotaciones
    // sin perder lo que el usuario está escribiendo

    private val _editAlias = MutableStateFlow("")
    val editAlias: StateFlow<String> = _editAlias.asStateFlow()

    private val _editNumPares = MutableStateFlow(GameConfiguration.DEFAULT_NUM_CARD_TYPES)
    val editNumPares: StateFlow<Int> = _editNumPares.asStateFlow()

    private val _editTimeEnabled = MutableStateFlow(false)
    val editTimeEnabled: StateFlow<Boolean> = _editTimeEnabled.asStateFlow()

    private val _editTimeText = MutableStateFlow("60")
    val editTimeText: StateFlow<String> = _editTimeText.asStateFlow()

    private var formInitialized = false

    // Inicializa el formulario con las preferencias guardadas (solo la primera vez)
    fun initFormIfNeeded(config: GameConfiguration) {
        if (!formInitialized) {
            _editAlias.value = config.playerName
            _editNumPares.value = config.numCardTypes
            _editTimeEnabled.value = config.timeLimit != null
            _editTimeText.value = (config.timeLimit ?: 60).toString()
            formInitialized = true
        }
    }

    fun onAliasChange(value: String)      { _editAlias.value = value }
    fun onNumParesChange(value: Int)       { _editNumPares.value = value }
    fun onTimeEnabledChange(value: Boolean){ _editTimeEnabled.value = value }
    fun onTimeTextChange(value: String)    { _editTimeText.value = value }

    // Construye la configuración actual del formulario
    fun buildCurrentConfig(): GameConfiguration {
        val timeLimit = if (_editTimeEnabled.value)
            _editTimeText.value.toIntOrNull()?.takeIf { it > 0 }
        else null
        return GameConfiguration(
            playerName   = _editAlias.value,
            numCardTypes = _editNumPares.value,
            timeLimit    = timeLimit
        )
    }

    // Persiste la configuración cuando el usuario confirma
    fun guardar(config: GameConfiguration) {
        viewModelScope.launch {
            repository.guardarPreferencias(config)
        }
        formInitialized = false // reset para la próxima apertura
    }
}

class PreferencesViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreferencesViewModel::class.java)) {
            return PreferencesViewModel(
                UserPreferencesRepository(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}