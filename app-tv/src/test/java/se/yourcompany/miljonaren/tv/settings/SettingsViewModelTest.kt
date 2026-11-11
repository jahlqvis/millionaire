package se.yourcompany.miljonaren.tv.settings

import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import se.yourcompany.miljonaren.domain.model.GameHistoryEntry
import se.yourcompany.miljonaren.domain.model.GameResult
import se.yourcompany.miljonaren.domain.model.GameSession
import se.yourcompany.miljonaren.domain.model.PlayerHistoryResult
import se.yourcompany.miljonaren.domain.usecase.GameHistoryRepository
import se.yourcompany.miljonaren.domain.usecase.GameSettingsStore
import se.yourcompany.miljonaren.tv.game.MainDispatcherRule

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun settings_saveSoundAndPruneWhenLimitChanges() = runTest {
        val store = FakeStore()
        val history = FakeHistory()
        val viewModel = SettingsViewModel(store, history)
        assertEquals(100, viewModel.state.value.historyLimit)
        viewModel.setSoundEnabled(false)
        viewModel.setHistoryLimit(25)
        advanceUntilIdle()
        assertFalse(store.soundEnabled)
        assertEquals(25, store.historyLimit)
        assertEquals(25, history.prunedTo)
        assertFalse(viewModel.state.value.isBusy)
    }

    @Test
    fun clearHistory_reportsErrors() = runTest {
        val history = FakeHistory().apply { failClear = true }
        val viewModel = SettingsViewModel(FakeStore(), history)
        viewModel.clearHistory()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.hasError)
        assertFalse(viewModel.state.value.isBusy)
        history.failClear = false
        viewModel.clearHistory()
        advanceUntilIdle()
        assertTrue(history.cleared)
        assertFalse(viewModel.state.value.hasError)
    }

    private class FakeStore : GameSettingsStore {
        override var historyLimit = 100
        override var soundEnabled = true
    }

    private class FakeHistory : GameHistoryRepository {
        var prunedTo = 0
        var cleared = false
        var failClear = false
        override suspend fun pruneHistory(limit: Int) { prunedTo = limit }
        override suspend fun clearHistory() { if (failClear) error("database error") else cleared = true }
        override suspend fun getRecentGames(): List<GameHistoryEntry> = emptyList()
        override suspend fun getResultsForSession(sessionId: String): List<PlayerHistoryResult> = emptyList()
        override suspend fun saveCompletedGame(session: GameSession, result: GameResult, completedAtEpochMs: Long) = Unit
    }
}
