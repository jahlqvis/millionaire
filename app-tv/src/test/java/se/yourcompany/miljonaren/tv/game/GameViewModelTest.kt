package se.yourcompany.miljonaren.tv.game

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import se.yourcompany.miljonaren.domain.model.AnswerOption
import se.yourcompany.miljonaren.domain.model.Difficulty
import se.yourcompany.miljonaren.domain.model.Question
import se.yourcompany.miljonaren.domain.usecase.AdvanceTurnUseCase
import se.yourcompany.miljonaren.domain.usecase.FinishGameUseCase
import se.yourcompany.miljonaren.domain.usecase.GetNextQuestionUseCase
import se.yourcompany.miljonaren.domain.usecase.QuestionRepository
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
    }

    @Test
    fun submitAnswer_wrongAnswerKeepsScoreUnchanged() = runTest {
        val viewModel = createViewModel(questionCount = 6, answerRevealDelayMs = 1_000L)
        viewModel.startGame(listOf("Anna", "Bengt"))

        viewModel.submitAnswer("B")

        val state = viewModel.uiState.value
        assertEquals(AnswerFeedback.WRONG, state.answerFeedback)
        assertEquals(0, state.session?.players?.first()?.score)
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
    fun finalRoundTransitionsToResults() = runTest {
        val viewModel = createViewModel(questionCount = 5, answerRevealDelayMs = 0L)
        viewModel.startGame(listOf("Anna"))

        repeat(5) {
            viewModel.submitAnswer("A")
            advanceUntilIdle()
        }

        val state = viewModel.uiState.value
        assertNotNull(state.result)
        assertNull(state.currentQuestion)
        assertEquals(5, state.result?.winner?.score)
    }

    @Test
    fun restartGame_clearsAllUiState() = runTest {
        val viewModel = createViewModel(questionCount = 6)
        viewModel.startGame(listOf("Anna", "Bengt"))

        viewModel.restartGame()

        val state = viewModel.uiState.value
        assertNull(state.session)
        assertNull(state.currentQuestion)
        assertNull(state.answerFeedback)
        assertFalse(state.answerLocked)
        assertNull(state.result)
    }

    private fun createViewModel(
        questionCount: Int,
        answerRevealDelayMs: Long = 0L
    ): GameViewModel {
        val repository = FakeQuestionRepository(questionCount)
        return GameViewModel(
            startGameUseCase = StartGameUseCase(),
            getNextQuestionUseCase = GetNextQuestionUseCase(repository),
            submitAnswerUseCase = SubmitAnswerUseCase(),
            advanceTurnUseCase = AdvanceTurnUseCase(),
            finishGameUseCase = FinishGameUseCase(),
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
}
