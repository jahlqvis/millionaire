package se.yourcompany.miljonaren.tv.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import se.yourcompany.miljonaren.domain.model.GameHistoryEntry
import se.yourcompany.miljonaren.domain.model.PlayerHistoryResult
import se.yourcompany.miljonaren.domain.usecase.GetGameResultsUseCase
import se.yourcompany.miljonaren.domain.usecase.GetRecentGamesUseCase

data class HistoryState(
    val isLoading: Boolean = false,
    val games: List<GameHistoryEntry> = emptyList(),
    val selectedSessionId: String? = null,
    val selectedResults: List<PlayerHistoryResult> = emptyList()
)

class HistoryViewModel(
    private val getRecentGamesUseCase: GetRecentGamesUseCase,
    private val getGameResultsUseCase: GetGameResultsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    fun loadHistory() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val games = getRecentGamesUseCase()
            val firstSessionId = games.firstOrNull()?.sessionId
            val results = if (firstSessionId != null) {
                getGameResultsUseCase(firstSessionId)
            } else {
                emptyList()
            }
            _state.value = HistoryState(
                isLoading = false,
                games = games,
                selectedSessionId = firstSessionId,
                selectedResults = results
            )
        }
    }

    fun selectSession(sessionId: String) {
        if (_state.value.selectedSessionId == sessionId) {
            return
        }
        viewModelScope.launch {
            val results = getGameResultsUseCase(sessionId)
            _state.update {
                it.copy(
                    selectedSessionId = sessionId,
                    selectedResults = results
                )
            }
        }
    }
}
