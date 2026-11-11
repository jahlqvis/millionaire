package se.yourcompany.miljonaren.tv.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import se.yourcompany.miljonaren.domain.usecase.GameHistoryRepository
import se.yourcompany.miljonaren.domain.usecase.GameSettingsStore

data class SettingsState(
    val historyLimit: Int,
    val soundEnabled: Boolean,
    val isBusy: Boolean = false,
    val hasError: Boolean = false
)

class SettingsViewModel(
    private val store: GameSettingsStore,
    private val history: GameHistoryRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState(store.historyLimit, store.soundEnabled))
    val state = _state.asStateFlow()

    fun setSoundEnabled(enabled: Boolean) {
        store.soundEnabled = enabled
        _state.update { it.copy(soundEnabled = enabled) }
    }

    fun setHistoryLimit(limit: Int) {
        if (_state.value.isBusy || limit !in 1..1000) return
        store.historyLimit = limit
        _state.update { it.copy(historyLimit = limit) }
        runHistoryOperation { history.pruneHistory(limit) }
    }

    fun clearHistory() {
        if (_state.value.isBusy) return
        runHistoryOperation { history.clearHistory() }
    }

    private fun runHistoryOperation(operation: suspend () -> Unit) {
        _state.update { it.copy(isBusy = true, hasError = false) }
        viewModelScope.launch {
            try {
                operation()
                _state.update { it.copy(isBusy = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isBusy = false, hasError = true) }
            }
        }
    }
}
