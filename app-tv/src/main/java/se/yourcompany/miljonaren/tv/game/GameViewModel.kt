package se.yourcompany.miljonaren.tv.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import se.yourcompany.miljonaren.data.repository.LocalQuestionRepository
import se.yourcompany.miljonaren.domain.model.GameStatus
import se.yourcompany.miljonaren.domain.usecase.AdvanceTurnUseCase
import se.yourcompany.miljonaren.domain.usecase.FinishGameUseCase
import se.yourcompany.miljonaren.domain.usecase.GetNextQuestionUseCase
import se.yourcompany.miljonaren.domain.usecase.StartGameUseCase
import se.yourcompany.miljonaren.domain.usecase.SubmitAnswerUseCase

class GameViewModel(
    private val startGameUseCase: StartGameUseCase = StartGameUseCase(),
    private val getNextQuestionUseCase: GetNextQuestionUseCase =
        GetNextQuestionUseCase(LocalQuestionRepository()),
    private val submitAnswerUseCase: SubmitAnswerUseCase = SubmitAnswerUseCase(),
    private val advanceTurnUseCase: AdvanceTurnUseCase = AdvanceTurnUseCase(),
    private val finishGameUseCase: FinishGameUseCase = FinishGameUseCase(),
    private val answerRevealDelayMs: Long = 1200L
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun startGame(playerNames: List<String>) {
        val startedSession = startGameUseCase(playerNames)
        val next = getNextQuestionUseCase(startedSession)

        if (next.question == null) {
            _uiState.value = GameUiState(
                session = next.updatedSession,
                result = finishGameUseCase(next.updatedSession)
            )
            return
        }

        _uiState.value = GameUiState(
            session = next.updatedSession,
            currentQuestion = next.question,
            answerLocked = false,
            answerFeedback = null,
            result = null
        )
    }

    fun submitAnswer(selectedOptionId: String) {
        val currentState = _uiState.value
        val activeSession = currentState.session ?: return
        val activeQuestion = currentState.currentQuestion ?: return
        if (currentState.answerLocked) {
            return
        }

        val evaluated = submitAnswerUseCase(
            session = activeSession,
            question = activeQuestion,
            selectedOptionId = selectedOptionId
        )

        _uiState.update {
            it.copy(
                session = evaluated.updatedSession,
                currentQuestion = activeQuestion,
                answerFeedback = if (evaluated.isCorrect) "Ratt svar!" else "Fel svar",
                answerLocked = true
            )
        }

        viewModelScope.launch {
            delay(answerRevealDelayMs)

            val progressed = advanceTurnUseCase(evaluated.updatedSession)

            if (progressed.status == GameStatus.FINISHED) {
                _uiState.value = GameUiState(
                    session = progressed,
                    result = finishGameUseCase(progressed)
                )
                return@launch
            }

            val next = getNextQuestionUseCase(progressed)
            if (next.question == null || next.updatedSession.status == GameStatus.FINISHED) {
                _uiState.value = GameUiState(
                    session = next.updatedSession,
                    result = finishGameUseCase(next.updatedSession)
                )
                return@launch
            }

            _uiState.value = GameUiState(
                session = next.updatedSession,
                currentQuestion = next.question,
                answerLocked = false,
                answerFeedback = null,
                result = null
            )
        }
    }

    fun restartGame() {
        _uiState.value = GameUiState()
    }
}
