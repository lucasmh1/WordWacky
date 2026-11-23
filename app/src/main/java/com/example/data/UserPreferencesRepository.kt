package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.VoiceStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.wordWackyDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "wordwacky_prefs"
)

data class UserPrefs(
    val voiceStyle: VoiceStyle = VoiceStyle.STORYTELLER,
    val partyModeEnabled: Boolean = false,
    val partyPlayerNames: List<String> = listOf("Captain Giggles", "Professor Chaos")
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val VOICE_STYLE = stringPreferencesKey("voice_style")
        val PARTY_MODE = booleanPreferencesKey("party_mode_enabled")
        val PARTY_PLAYERS = stringPreferencesKey("party_player_names")
    }

    val prefsFlow: Flow<UserPrefs> = context.wordWackyDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val styleName = prefs[Keys.VOICE_STYLE]
            val voiceStyle = VoiceStyle.entries.firstOrNull { it.name == styleName }
                ?: VoiceStyle.STORYTELLER
            val partyEnabled = prefs[Keys.PARTY_MODE] ?: false
            val rawNames = prefs[Keys.PARTY_PLAYERS]
            val names = rawNames
                ?.split("|")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?.takeIf { it.size >= 2 }
                ?: listOf("Captain Giggles", "Professor Chaos")
            UserPrefs(
                voiceStyle = voiceStyle,
                partyModeEnabled = partyEnabled,
                partyPlayerNames = names
            )
        }

    suspend fun setVoiceStyle(style: VoiceStyle) {
        context.wordWackyDataStore.edit { prefs ->
            prefs[Keys.VOICE_STYLE] = style.name
        }
    }

    suspend fun setPartyModeEnabled(enabled: Boolean) {
        context.wordWackyDataStore.edit { prefs ->
            prefs[Keys.PARTY_MODE] = enabled
        }
    }

    suspend fun setPartyPlayerNames(names: List<String>) {
        context.wordWackyDataStore.edit { prefs ->
            prefs[Keys.PARTY_PLAYERS] = names.joinToString("|") { it.replace("|", " ") }
        }
    }
}
