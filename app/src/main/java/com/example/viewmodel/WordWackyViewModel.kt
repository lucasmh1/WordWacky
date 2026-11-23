package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.StoryCatalog
import com.example.data.UserPreferencesRepository
import com.example.model.CompletedStory
import com.example.model.FilledBlank
import com.example.model.PartOfSpeech
import com.example.model.PartyPlayer
import com.example.model.StoryCategory
import com.example.model.StoryTemplate
import com.example.model.VoiceStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class HomeTab {
    STORIES,
    PARTY,
    LAUGH_BOOK
}

sealed class AppScreen {
    data object Home : AppScreen()
    data object WordStudio : AppScreen()
    data object StoryReveal : AppScreen()
}

data class StudioSession(
    val template: StoryTemplate,
    val currentBlankIndex: Int = 0,
    val enteredWords: Map<String, String> = emptyMap(),
    val enteredByPlayer: Map<String, String> = emptyMap(),
    val sparkIdeas: Map<String, List<String>> = emptyMap(),
    val isPartySession: Boolean = false
) {
    val currentBlank = template.blanks[currentBlankIndex.coerceIn(0, template.blanks.lastIndex)]
    val totalBlanks: Int get() = template.blanks.size
    val filledCount: Int get() = template.blanks.count { !enteredWords[it.id].isNullOrBlank() }
    val isLastBlank: Boolean get() = currentBlankIndex >= template.blanks.lastIndex
    val hasAnyTypedWords: Boolean get() = enteredWords.values.any { it.isNotBlank() }
}

data class WordWackyUiState(
    val currentScreen: AppScreen = AppScreen.Home,
    val activeTab: HomeTab = HomeTab.STORIES,
    val selectedCategory: StoryCategory? = null,
    val stories: List<StoryTemplate> = StoryCatalog.allStories,
    val playedTemplateIds: Set<String> = emptySet(),
    val studioSession: StudioSession? = null,
    val activeStory: CompletedStory? = null,
    val isDramaticRevealMode: Boolean = false,
    val revealedBlankIds: Set<String> = emptySet(),
    val confettiTriggerCount: Int = 0,
    val laughBook: List<CompletedStory> = emptyList(),
    val selectedVoiceStyle: VoiceStyle = VoiceStyle.STORYTELLER,
    val isPartyModeEnabled: Boolean = false,
    val partyPlayers: List<PartyPlayer> = listOf(
        PartyPlayer(id = "p1", name = "Captain Giggles", colorIndex = 0),
        PartyPlayer(id = "p2", name = "Professor Chaos", colorIndex = 1)
    )
)

class WordWackyViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = UserPreferencesRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(WordWackyUiState())
    val uiState: StateFlow<WordWackyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            prefsRepo.prefsFlow.collect { prefs ->
                _uiState.update { state ->
                    state.copy(
                        selectedVoiceStyle = prefs.voiceStyle,
                        isPartyModeEnabled = prefs.partyModeEnabled,
                        partyPlayers = prefs.partyPlayerNames.mapIndexed { idx, name ->
                            PartyPlayer(
                                id = "player_${idx + 1}",
                                name = name,
                                colorIndex = idx % 7
                            )
                        }
                    )
                }
            }
        }
    }

    fun selectTab(tab: HomeTab) {
        _uiState.update { it.copy(activeTab = tab, currentScreen = AppScreen.Home) }
    }

    fun selectCategory(category: StoryCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun startStory(template: StoryTemplate, forcePartyMode: Boolean? = null) {
        val state = _uiState.value
        val useParty = forcePartyMode ?: state.isPartyModeEnabled
        val ideasMap = template.blanks.associate { blank ->
            blank.id to blank.partOfSpeech.suggestions.shuffled().take(3)
        }
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.WordStudio,
                studioSession = StudioSession(
                    template = template,
                    currentBlankIndex = 0,
                    enteredWords = emptyMap(),
                    enteredByPlayer = emptyMap(),
                    sparkIdeas = ideasMap,
                    isPartySession = useParty
                )
            )
        }
    }

    fun startRandomStory(excludeId: String? = null, forcePartyMode: Boolean? = null) {
        val pool = StoryCatalog.allStories.filter { it.id != excludeId }
        val unplayed = pool.filter { it.id !in _uiState.value.playedTemplateIds }
        val picked = (unplayed.ifEmpty { pool }).random()
        startStory(picked, forcePartyMode)
    }

    fun getPlayerNameForBlankIndex(blankIndex: Int): String? {
        val state = _uiState.value
        val session = state.studioSession ?: return null
        if (!session.isPartySession || state.partyPlayers.isEmpty()) return null
        val player = state.partyPlayers[blankIndex % state.partyPlayers.size]
        return player.name.ifBlank { "Player ${(blankIndex % state.partyPlayers.size) + 1}" }
    }

    fun getPlayerColorForBlankIndex(blankIndex: Int): Int {
        val state = _uiState.value
        if (state.partyPlayers.isEmpty()) return 0
        return state.partyPlayers[blankIndex % state.partyPlayers.size].colorIndex
    }

    fun updateCurrentWord(text: String) {
        _uiState.update { state ->
            val session = state.studioSession ?: return@update state
            val blankId = session.currentBlank.id
            val playerName = getPlayerNameForBlankIndex(session.currentBlankIndex)
            val updatedBy = if (playerName != null) {
                session.enteredByPlayer + (blankId to playerName)
            } else {
                session.enteredByPlayer
            }
            state.copy(
                studioSession = session.copy(
                    enteredWords = session.enteredWords + (blankId to text),
                    enteredByPlayer = updatedBy
                )
            )
        }
    }

    fun fillCurrentWithRandomWord() {
        val session = _uiState.value.studioSession ?: return
        val pos = session.currentBlank.partOfSpeech
        val currentVal = session.enteredWords[session.currentBlank.id].orEmpty().trim()
        val choices = pos.suggestions.filterNot { it.equals(currentVal, ignoreCase = true) }
        val randomPick = (choices.ifEmpty { pos.suggestions }).random()
        updateCurrentWord(randomPick)
    }

    fun refreshSparkIdeasForCurrentBlank() {
        _uiState.update { state ->
            val session = state.studioSession ?: return@update state
            val blank = session.currentBlank
            val newIdeas = blank.partOfSpeech.suggestions.shuffled().take(3)
            state.copy(
                studioSession = session.copy(
                    sparkIdeas = session.sparkIdeas + (blank.id to newIdeas)
                )
            )
        }
    }

    fun jumpToBlank(index: Int) {
        _uiState.update { state ->
            val session = state.studioSession ?: return@update state
            val clamped = index.coerceIn(0, session.template.blanks.lastIndex)
            state.copy(studioSession = session.copy(currentBlankIndex = clamped))
        }
    }

    fun goToPreviousBlank() {
        val session = _uiState.value.studioSession ?: return
        if (session.currentBlankIndex > 0) {
            jumpToBlank(session.currentBlankIndex - 1)
        }
    }

    fun advanceOrFinishStudio() {
        val state = _uiState.value
        val session = state.studioSession ?: return
        val blankId = session.currentBlank.id
        // If current word is empty when tapping Next/Reveal, gently auto-fill a fun suggestion
        if (session.enteredWords[blankId].isNullOrBlank()) {
            fillCurrentWithRandomWord()
        }
        val refreshed = _uiState.value.studioSession ?: return
        if (!refreshed.isLastBlank) {
            jumpToBlank(refreshed.currentBlankIndex + 1)
        } else {
            completeCurrentStory()
        }
    }

    fun autoFillRemainingAndReveal() {
        val state = _uiState.value
        val session = state.studioSession ?: return
        val filledMap = session.enteredWords.toMutableMap()
        val byMap = session.enteredByPlayer.toMutableMap()
        session.template.blanks.forEachIndexed { idx, blank ->
            if (filledMap[blank.id].isNullOrBlank()) {
                filledMap[blank.id] = pickFreshSuggestion(blank.partOfSpeech, filledMap.values)
                if (session.isPartySession && state.partyPlayers.isNotEmpty()) {
                    val player = state.partyPlayers[idx % state.partyPlayers.size]
                    byMap[blank.id] = player.name.ifBlank { "Player ${(idx % state.partyPlayers.size) + 1}" }
                }
            }
        }
        _uiState.update {
            it.copy(
                studioSession = session.copy(
                    enteredWords = filledMap,
                    enteredByPlayer = byMap
                )
            )
        }
        completeCurrentStory()
    }

    private fun pickFreshSuggestion(pos: PartOfSpeech, usedWords: Collection<String>): String {
        val lowerUsed = usedWords.map { it.lowercase() }.toSet()
        val fresh = pos.suggestions.filter { it.lowercase() !in lowerUsed }
        return (fresh.ifEmpty { pos.suggestions }).random()
    }

    private fun completeCurrentStory() {
        val state = _uiState.value
        val session = state.studioSession ?: return
        val filledBlanks = session.template.blanks.associate { blank ->
            val word = session.enteredWords[blank.id]?.trim().takeUnless { it.isNullOrBlank() }
                ?: blank.partOfSpeech.suggestions.random()
            blank.id to FilledBlank(
                blankId = blank.id,
                word = word,
                enteredByPlayer = session.enteredByPlayer[blank.id]
            )
        }
        val completed = CompletedStory(
            id = UUID.randomUUID().toString(),
            template = session.template,
            filledBlanks = filledBlanks,
            createdAtMillis = System.currentTimeMillis(),
            laughRating = 0,
            wasPartyMode = session.isPartySession
        )
        val startDramatic = session.isPartySession
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.StoryReveal,
                studioSession = null,
                activeStory = completed,
                isDramaticRevealMode = startDramatic,
                revealedBlankIds = if (startDramatic) emptySet() else filledBlanks.keys,
                confettiTriggerCount = it.confettiTriggerCount + 1,
                laughBook = listOf(completed) + it.laughBook,
                playedTemplateIds = it.playedTemplateIds + session.template.id
            )
        }
    }

    fun openCompletedStory(story: CompletedStory) {
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.StoryReveal,
                activeStory = story,
                isDramaticRevealMode = false,
                revealedBlankIds = story.filledBlanks.keys
            )
        }
    }

    fun toggleDramaticRevealMode(dramatic: Boolean) {
        _uiState.update { state ->
            val allIds = state.activeStory?.filledBlanks?.keys.orEmpty()
            state.copy(
                isDramaticRevealMode = dramatic,
                revealedBlankIds = if (dramatic) emptySet() else allIds
            )
        }
    }

    fun revealSingleBlank(blankId: String) {
        _uiState.update { state ->
            state.copy(revealedBlankIds = state.revealedBlankIds + blankId)
        }
    }

    fun revealAllBlanks() {
        _uiState.update { state ->
            val allIds = state.activeStory?.filledBlanks?.keys.orEmpty()
            state.copy(revealedBlankIds = allIds)
        }
    }

    fun updateWordInActiveStory(blankId: String, newWord: String) {
        val trimmed = newWord.trim()
        if (trimmed.isEmpty()) return
        _uiState.update { state ->
            val active = state.activeStory ?: return@update state
            val oldFilled = active.filledBlanks[blankId] ?: return@update state
            val updatedFilled = active.filledBlanks + (blankId to oldFilled.copy(word = trimmed))
            val updatedStory = active.copy(filledBlanks = updatedFilled)
            state.copy(
                activeStory = updatedStory,
                laughBook = state.laughBook.map { if (it.id == updatedStory.id) updatedStory else it }
            )
        }
    }

    fun rateActiveStory(rating: Int) {
        val clamped = rating.coerceIn(1, 5)
        _uiState.update { state ->
            val active = state.activeStory ?: return@update state
            val updatedStory = active.copy(laughRating = clamped)
            state.copy(
                activeStory = updatedStory,
                laughBook = state.laughBook.map { if (it.id == updatedStory.id) updatedStory else it }
            )
        }
    }

    fun deleteCompletedStory(storyId: String) {
        _uiState.update { state ->
            state.copy(
                laughBook = state.laughBook.filterNot { it.id == storyId },
                activeStory = if (state.activeStory?.id == storyId) null else state.activeStory
            )
        }
    }

    fun clearLaughBook() {
        _uiState.update { it.copy(laughBook = emptyList()) }
    }

    fun returnHome(targetTab: HomeTab? = null) {
        _uiState.update { state ->
            state.copy(
                currentScreen = AppScreen.Home,
                studioSession = null,
                activeTab = targetTab ?: state.activeTab
            )
        }
    }

    fun setVoiceStyle(style: VoiceStyle) {
        _uiState.update { it.copy(selectedVoiceStyle = style) }
        viewModelScope.launch {
            prefsRepo.setVoiceStyle(style)
        }
    }

    fun setPartyModeEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isPartyModeEnabled = enabled) }
        viewModelScope.launch {
            prefsRepo.setPartyModeEnabled(enabled)
        }
    }

    fun updatePartyPlayerName(playerId: String, newName: String) {
        val updatedPlayers = _uiState.value.partyPlayers.map { player ->
            if (player.id == playerId) player.copy(name = newName) else player
        }
        _uiState.update { it.copy(partyPlayers = updatedPlayers) }
        persistPlayers(updatedPlayers)
    }

    fun addPartyPlayer() {
        val current = _uiState.value.partyPlayers
        if (current.size >= 4) return
        val funDefaults = listOf("Captain Giggles", "Professor Chaos", "Sir Chuckles", "DJ Waffles")
        val nextName = funDefaults.getOrElse(current.size) { "Player ${current.size + 1}" }
        val updated = current + PartyPlayer(
            id = "player_${System.currentTimeMillis()}",
            name = nextName,
            colorIndex = current.size % 7
        )
        _uiState.update { it.copy(partyPlayers = updated) }
        persistPlayers(updated)
    }

    fun removePartyPlayer(playerId: String) {
        val current = _uiState.value.partyPlayers
        if (current.size <= 2) return
        val updated = current.filterNot { it.id == playerId }
        _uiState.update { it.copy(partyPlayers = updated) }
        persistPlayers(updated)
    }

    private fun persistPlayers(players: List<PartyPlayer>) {
        viewModelScope.launch {
            val cleaned = players.mapIndexed { idx, p ->
                p.name.trim().ifEmpty { "Player ${idx + 1}" }
            }
            prefsRepo.setPartyPlayerNames(cleaned)
        }
    }
}
