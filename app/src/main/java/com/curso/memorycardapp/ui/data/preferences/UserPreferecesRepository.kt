package com.curso.memorycardapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.curso.memorycardapp.ui.model.GameConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para crear el DataStore una sola vez a nivel de Context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    // Claves para cada preferencia
    private object Keys {
        val ALIAS         = stringPreferencesKey("alias")
        val NUM_PARES     = intPreferencesKey("num_pares")
        val TIME_ENABLED  = booleanPreferencesKey("time_enabled")
        val TIME_LIMIT    = intPreferencesKey("time_limit")
    }

    val userPreferences: Flow<GameConfiguration> = context.dataStore.data
        .map { prefs ->
            GameConfiguration(
                playerName   = prefs[Keys.ALIAS] ?: "",
                numCardTypes = prefs[Keys.NUM_PARES] ?: GameConfiguration.DEFAULT_NUM_CARD_TYPES,
                timeLimit    = if (prefs[Keys.TIME_ENABLED] == true)
                    prefs[Keys.TIME_LIMIT] ?: 60
                else null
            )
        }

    // Guarda la configuración actual como preferencias del usuario.
    suspend fun guardarPreferencias(config: GameConfiguration) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ALIAS]        = config.playerName
            prefs[Keys.NUM_PARES]    = config.numCardTypes
            prefs[Keys.TIME_ENABLED] = config.timeLimit != null
            prefs[Keys.TIME_LIMIT]   = config.timeLimit ?: 60
        }
    }
}