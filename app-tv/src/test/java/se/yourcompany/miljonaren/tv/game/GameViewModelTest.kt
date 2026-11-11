package se.yourcompany.miljonaren.tv.game

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.runCurrent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import se.yourcompany.miljonaren.core.audio.SoundEffect
import se.yourcompany.miljonaren.domain.model.AnswerOption
import se.yourcompany.miljonaren.domain.model.Difficulty
import se.yourcompany.miljonaren.domain.model.GameResult
import se.yourcompany.miljonaren.domain.model.GameSession
import se.yourcompany.miljonaren.domain.model.GameHistoryEntry
import se.yourcompany.miljonaren.domain.model.Question
import se.yourcompany.miljonaren.domain.model.PlayerHistoryResult
import se.yourcompany.miljonaren.domain.usecase.ApplyFiftyFiftyUseCase
import se.yourcompany.miljonaren.domain.usecase.AdvanceTurnUseCase
import se.yourcompany.miljonaren.domain.usecase.FinishGameUseCase
import se.yourcompany.miljonaren.domain.usecase.GameHistoryRepository
import se.yourcompany.miljonaren.domain.usecase.GetNextQuestionUseCase
import se.yourcompany.miljonaren.domain.usecase.QuestionRepository
import se.yourcompany.miljonaren.domain.usecase.SaveCompletedGameUseCase
import se.yourcompany.miljonaren.domain.usecase.StartGameUseCase
import se.yourcompany.miljonaren.domain.usecase.SubmitAnswerUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun startGame_loadsFirstQuestionIntoUiState() = runTest {
        val viewModel = createViewModel(questionCount = 6)

        viewModel.startGame(listOf("Anna", "Bengt"))

        val state = viewModel.uiState.value
        assertNotNull(state.session)
        assertEquals("Fråga 1", state.currentQuestion?.textSv)
        assertNull(state.result)
        assertFalse(state.answerLocked)
        assertNull(state.remainingOptionIds)
    }

    @Test
    fun startGame_usesSelectedRounds() = runTest {
        val viewModel = createViewModel(questionCount = 6)
        viewModel.startGame(listOf("Anna"), maxRounds = 3)
        assertEquals(3, viewModel.uiState.value.session?.maxRounds)
    }

    @Test
    fun answerFeedback_remainsVisibleForFiveSeconds() = runTest {
        val viewModel = createViewModel(questionCount = 6, answerRevealDelayMs = 5_000L)
        viewModel.startGame(listOf("Anna"))
        viewModel.submitAnswer("B")

        advanceTimeBy(4_999L)
        assertEquals(AnswerFeedback.WRONG, viewModel.uiState.value.answerFeedback)
        assertEquals("A", viewModel.uiState.value.currentQuestion?.correctOptionId)
        advanceTimeBy(1L)
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.answerFeedback)
        assertEquals("Fråga 2", viewModel.uiState.value.currentQuestion?.textSv)
    }

    @Test
    fun timer_pausesAndResumesThenTimesOutWithoutScoring() = runTest {
        val viewModel = createViewModel(questionCount = 6, answerRevealDelayMs = 5_000L)
        viewModel.startGame(listOf("Anna"), timerEnabled = true)
        runCurrent()
        advanceTimeBy(5_000L)
        runCurrent()
        assertEquals(25_000L, viewModel.uiState.value.remainingTimerMs)

        viewModel.pauseGame()
        advanceTimeBy(40_000L)
        runCurrent()
        assertEquals(25_000L, viewModel.uiState.value.remainingTimerMs)
        assertFalse(viewModel.uiState.value.answerLocked)

        viewModel.resumeGame()
        runCurrent()
        advanceTimeBy(25_000L)
        runCurrent()
        assertEquals(AnswerFeedback.TIMEOUT, viewModel.uiState.value.answerFeedback)
        assertEquals(0, viewModel.uiState.value.session?.players?.first()?.score)
        assertTrue(viewModel.uiState.value.answerLocked)
        advanceTimeBy(5_000L)
        runCurrent()
        assertEquals("Fråga 2", viewModel.uiState.value.currentQuestion?.textSv)
        assertEquals(30_000L, viewModel.uiState.value.remainingTimerMs)
        viewModel.restartGame()
    }

    @Test
    fun pauseDuringReveal_preservesRemainingRevealTime() = runTest {
        val viewModel = createViewModel(questionCount = 6, answerRevealDelayMs = 5_000L)
        viewModel.startGame(listOf("Anna"))
        viewModel.submitAnswer("A")
        runCurrent()
        advanceTimeBy(2_000L)
        runCurrent()
        viewModel.pauseGame()
        advanceTimeBy(10_000L)
        runCurrent()
        assertEquals("Fråga 1", viewModel.uiState.value.currentQuestion?.textSv)
        assertEquals(AnswerFeedback.CORRECT, viewModel.uiState.value.answerFeedback)
        viewModel.resumeGame()
        runCurrent()
        advanceTimeBy(3_000L)
        runCurrent()
        assertEquals("Fråga 2", viewModel.uiState.value.currentQuestion?.textSv)
    }

    @Test
    fun restartGame_cancelsPendingAnswerReveal() = runTest {
        val viewModel = createViewModel(questionCount = 6, answerRevealDelayMs = 5_000L)
        viewModel.startGame(listOf("Anna"))
        viewModel.submitAnswer("A")
        viewModel.restartGame()
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.session)
        assertNull(viewModel.uiState.value.result)
    }

    @Test
    fun removedAnswer_cannotBeSubmitted() = runTest {
        val viewModel = createViewModel(questionCount = 6)
        viewModel.startGame(listOf("Anna"))
        viewModel.useFiftyFifty()
        viewModel.submitAnswer("C")
        assertFalse(viewModel.uiState.value.answerLocked)
        assertNull(viewModel.uiState.value.answerFeedback)
    }

    @Test
    fun submitAnswer_correctAnswerSetsFeedbackAndIncrementsScore() = runTest {
        val viewModel = createViewModel(questionCount = 6, answerRevealDelayMs = 1_000L)
        viewModel.startGame(listOf("Anna", "Bengt"))

        viewModel.submitAnswer("A")

        val state = viewModel.uiState.value
        assertEquals(AnswerFeedback.CORRECT, state.answerFeedback)
        assertTrue(state.answerLocked)
        assertEquals(1, state.session?.players?.first()?.score)

        advanceTimeBy(1_000L)
        advanceUntilIdle()

        val advanced = viewModel.uiState.value
        assertNull(advanced.answerFeedback)
        assertFalse(advanced.answerLocked)
        assertEquals("Bengt", advanced.session?.activePlayer?.name)
        assertNull(advanced.remainingOptionIds)
        assertTrue(viewModel.audioEvents.replayCache.contains(SoundEffect.ANSWER_SELECTED))
        assertTrue(viewModel.audioEvents.replayCache.contains(SoundEffect.ANSWER_CORRECT))
    }

    @Test
    fun submitAnswer_wrongAnswerKeepsScoreUnchanged() = runTest {
        val viewModel = createViewModel(questionCount = 6, answerRevealDelayMs = 1_000L)
        viewModel.startGame(listOf("Anna", "Bengt"))

        viewModel.submitAnswer("B")

        val state = viewModel.uiState.value
        assertEquals(AnswerFeedback.WRONG, state.answerFeedback)
        assertEquals(0, state.session?.players?.first()?.score)
        assertTrue(viewModel.audioEvents.replayCache.contains(SoundEffect.ANSWER_SELECTED))
        assertTrue(viewModel.audioEvents.replayCache.contains(SoundEffect.ANSWER_WRONG))
    }

    @Test
    fun submitAnswer_afterRevealAdvancesToNextPlayer() = runTest {
        val viewModel = createViewModel(questionCount = 6, answerRevealDelayMs = 500L)
        viewModel.startGame(listOf("Anna", "Bengt"))

        viewModel.submitAnswer("A")
        advanceTimeBy(500L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Bengt", state.session?.activePlayer?.name)
        assertEquals(1, state.session?.currentRound)
        assertEquals("Fråga 2", state.currentQuestion?.textSv)
    }

    @Test
    fun useFiftyFifty_keepsOnlyTwoOptionIds() = runTest {
        val viewModel = createViewModel(questionCount = 6)
        viewModel.startGame(listOf("Anna", "Bengt"))

        viewModel.useFiftyFifty()

        val state = viewModel.uiState.value
        assertEquals(setOf("A", "B"), state.remainingOptionIds)
    }

    @Test
    fun useFiftyFifty_cannotBeUsedTwiceBySamePlayer() = runTest {
        val viewModel = createViewModel(questionCount = 6)
        viewModel.startGame(listOf("Anna", "Bengt"))

        viewModel.useFiftyFifty()
        val first = viewModel.uiState.value.remainingOptionIds

        viewModel.useFiftyFifty()
        val second = viewModel.uiState.value.remainingOptionIds

        assertEquals(first, second)
    }

    @Test
    fun useFiftyFifty_resetsOnNextQuestion() = runTest {
        val viewModel = createViewModel(questionCount = 6, answerRevealDelayMs = 500L)
        viewModel.startGame(listOf("Anna", "Bengt"))
        viewModel.useFiftyFifty()
        assertNotNull(viewModel.uiState.value.remainingOptionIds)

        viewModel.submitAnswer("A")
        advanceTimeBy(500L)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.remainingOptionIds)
    }

    @Test
    fun finalRoundTransitionsToResults() = runTest {
        val fakeHistoryRepository = FakeGameHistoryRepository()
        val viewModel = createViewModel(
            questionCount = 5,
            answerRevealDelayMs = 0L,
            gameHistoryRepository = fakeHistoryRepository
        )
        viewModel.startGame(listOf("Anna"))

        repeat(5) {
            viewModel.submitAnswer("A")
            advanceUntilIdle()
        }

        val state = viewModel.uiState.value
        assertNotNull(state.result)
        assertNull(state.currentQuestion)
        assertEquals(5, state.result?.winner?.score)
        assertEquals(1, fakeHistoryRepository.savedSessionIds.size)
        assertTrue(viewModel.audioEvents.replayCache.contains(SoundEffect.GAME_COMPLETE))
    }

    @Test
    fun restartGame_clearsAllUiState() = runTest {
        val viewModel = createViewModel(questionCount = 6)
        viewModel.startGame(listOf("Anna", "Bengt"))

        viewModel.restartGame()

        val state = viewModel.uiState.value
        assertNull(state.session)
        assertNull(state.currentQuestion)
        assertNull(state.remainingOptionIds)
        assertNull(state.answerFeedback)
        assertFalse(state.answerLocked)
        assertNull(state.result)
    }

    private fun createViewModel(
        questionCount: Int,
        answerRevealDelayMs: Long = 0L,
        gameHistoryRepository: GameHistoryRepository = FakeGameHistoryRepository()
    ): GameViewModel {
        val repository = FakeQuestionRepository(questionCount)
        return GameViewModel(
            startGameUseCase = StartGameUseCase(),
            getNextQuestionUseCase = GetNextQuestionUseCase(repository),
            applyFiftyFiftyUseCase = ApplyFiftyFiftyUseCase(),
            submitAnswerUseCase = SubmitAnswerUseCase(),
            advanceTurnUseCase = AdvanceTurnUseCase(),
            finishGameUseCase = FinishGameUseCase(),
            saveCompletedGameUseCase = SaveCompletedGameUseCase(gameHistoryRepository),
            answerRevealDelayMs = answerRevealDelayMs
        )
    }

    private class FakeQuestionRepository(questionCount: Int) : QuestionRepository {
        private val questions = (1..questionCount).map { index ->
            Question(
                id = "q$index",
                textSv = "Fråga $index",
                options = listOf(
                    AnswerOption(id = "A", textSv = "Alternativ A"),
                    AnswerOption(id = "B", textSv = "Alternativ B"),
                    AnswerOption(id = "C", textSv = "Alternativ C"),
                    AnswerOption(id = "D", textSv = "Alternativ D")
                ),
                correctOptionId = "A",
                category = "Test",
                difficulty = Difficulty.EASY
            )
        }

        override fun getNextUnusedQuestion(askedIds: Set<String>): Question? {
            return questions.firstOrNull { it.id !in askedIds }
        }
    }

    private class FakeGameHistoryRepository : GameHistoryRepository {
        val savedSessionIds = mutableListOf<String>()

        override suspend fun saveCompletedGame(
            session: GameSession,
            result: GameResult,
            completedAtEpochMs: Long
        ) {
            savedSessionIds += session.id
        }

        override suspend fun getRecentGames(): List<GameHistoryEntry> = emptyList()

        override suspend fun getResultsForSession(sessionId: String): List<PlayerHistoryResult> = emptyList()
    }
}
