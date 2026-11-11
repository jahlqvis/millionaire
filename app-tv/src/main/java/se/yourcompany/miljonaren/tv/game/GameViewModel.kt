package se.yourcompany.miljonaren.tv.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import se.yourcompany.miljonaren.core.audio.SoundEffect
import se.yourcompany.miljonaren.data.repository.LocalQuestionRepository
import se.yourcompany.miljonaren.domain.model.GameResult
import se.yourcompany.miljonaren.domain.model.GameSession
import se.yourcompany.miljonaren.domain.model.GameStatus
import se.yourcompany.miljonaren.domain.model.LifelineType
import se.yourcompany.miljonaren.domain.usecase.ApplyFiftyFiftyUseCase
import se.yourcompany.miljonaren.domain.usecase.AdvanceTurnUseCase
import se.yourcompany.miljonaren.domain.usecase.FinishGameUseCase
import se.yourcompany.miljonaren.domain.usecase.GetNextQuestionUseCase
import se.yourcompany.miljonaren.domain.usecase.GetLifelineAdviceUseCase
import se.yourcompany.miljonaren.domain.usecase.NoOpGameHistoryRepository
import se.yourcompany.miljonaren.domain.usecase.SaveCompletedGameUseCase
import se.yourcompany.miljonaren.domain.usecase.StartGameUseCase
import se.yourcompany.miljonaren.domain.usecase.SubmitAnswerUseCase

class GameViewModel(
    private val startGameUseCase: StartGameUseCase = StartGameUseCase(),
    private val getNextQuestionUseCase: GetNextQuestionUseCase =
        GetNextQuestionUseCase(LocalQuestionRepository()),
    private val applyFiftyFiftyUseCase: ApplyFiftyFiftyUseCase = ApplyFiftyFiftyUseCase(),
    private val getLifelineAdviceUseCase: GetLifelineAdviceUseCase = GetLifelineAdviceUseCase(),
    private val submitAnswerUseCase: SubmitAnswerUseCase = SubmitAnswerUseCase(),
    private val advanceTurnUseCase: AdvanceTurnUseCase = AdvanceTurnUseCase(),
    private val finishGameUseCase: FinishGameUseCase = FinishGameUseCase(),
    private val saveCompletedGameUseCase: SaveCompletedGameUseCase =
        SaveCompletedGameUseCase(NoOpGameHistoryRepository()),
    private val answerRevealDelayMs: Long = 5000L,
    private val questionTimeLimitMs: Long = 30_000L
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    // Sound effects are one-shot events and must not replay on activity recreation.
    private val _audioEvents = MutableSharedFlow<SoundEffect>(extraBufferCapacity = 8)
    val audioEvents: SharedFlow<SoundEffect> = _audioEvents.asSharedFlow()
    private var timerJob: Job? = null
    private var revealJob: Job? = null
    private var revealRemainingMs = 0L

    fun startGame(playerNames: List<String>, maxRounds: Int = 5, timerEnabled: Boolean = false) {
        timerJob?.cancel()
        revealJob?.cancel()
        val startedSession = startGameUseCase(playerNames, maxRounds)
        getNextQuestionUseCase.startGame()
        val next = getNextQuestionUseCase(startedSession)

        if (next.question == null) {
            val gameResult = finishGameUseCase(next.updatedSession)
            _uiState.value = GameUiState(
                session = next.updatedSession,
                result = gameResult
            )
            _audioEvents.tryEmit(SoundEffect.GAME_COMPLETE)
            persistCompletedGame(next.updatedSession, gameResult)
            return
        }

        _uiState.value = GameUiState(
            session = next.updatedSession,
            currentQuestion = next.question,
            timerEnabled = timerEnabled,
            remainingTimerMs = if (timerEnabled) questionTimeLimitMs else null
        )
        scheduleTimer()
    }

    fun useFiftyFifty() {
        val currentState = _uiState.value
        val activeSession = currentState.session ?: return
        val activeQuestion = currentState.currentQuestion ?: return
        if (currentState.isPaused || currentState.advice != null || currentState.answerLocked || currentState.remainingOptionIds != null) {
            return
        }

        val result = runCatching {
            applyFiftyFiftyUseCase(activeSession, activeQuestion)
        }.getOrNull() ?: return

        _uiState.update {
            it.copy(
                session = result.updatedSession,
                remainingOptionIds = result.remainingOptionIds
            )
        }
    }

    fun useAdvice(type: LifelineType) {
        val state = _uiState.value
        val session = state.session ?: return
        val question = state.currentQuestion ?: return
        if (state.isPaused || state.answerLocked || state.advice != null) return
        val result = runCatching {
            getLifelineAdviceUseCase(session, question, type, state.remainingOptionIds)
        }.getOrNull() ?: return
        timerJob?.cancel()
        _uiState.update { it.copy(session = result.updatedSession, advice = result.advice) }
    }

    fun dismissAdvice() {
        if (_uiState.value.advice == null) return
        _uiState.update { it.copy(advice = null) }
        scheduleTimer()
    }

    fun submitAnswer(selectedOptionId: String) {
        submitAnswerOrTimeout(selectedOptionId)
    }

    private fun submitAnswerOrTimeout(selectedOptionId: String?) {
        val currentState = _uiState.value
        val activeSession = currentState.session ?: return
        val activeQuestion = currentState.currentQuestion ?: return
        if (currentState.isPaused || currentState.advice != null || currentState.answerLocked) {
            return
        }
        if (selectedOptionId != null &&
            (activeQuestion.options.none { it.id == selectedOptionId } ||
                currentState.remainingOptionIds?.contains(selectedOptionId) == false)
        ) return

        timerJob?.cancel()
        if (selectedOptionId != null) _audioEvents.tryEmit(SoundEffect.ANSWER_SELECTED)

        val evaluated = submitAnswerUseCase(
            session = activeSession,
            question = activeQuestion,
            selectedOptionId = selectedOptionId
        )

        _uiState.update {
            it.copy(
                session = evaluated.updatedSession,
                currentQuestion = activeQuestion,
                answerFeedback = when {
                    selectedOptionId == null -> AnswerFeedback.TIMEOUT
                    evaluated.isCorrect -> AnswerFeedback.CORRECT
                    else -> AnswerFeedback.WRONG
                },
                answerLocked = true
            )
        }

        _audioEvents.tryEmit(
            if (evaluated.isCorrect) SoundEffect.ANSWER_CORRECT else SoundEffect.ANSWER_WRONG
        )

        revealRemainingMs = answerRevealDelayMs
        scheduleReveal()
    }

    fun pauseGame() {
        if (_uiState.value.session == null || _uiState.value.result != null || _uiState.value.isPaused) return
        _uiState.update { it.copy(isPaused = true) }
        timerJob?.cancel()
        revealJob?.cancel()
    }

    fun resumeGame() {
        if (!_uiState.value.isPaused) return
        _uiState.update { it.copy(isPaused = false) }
        if (_uiState.value.answerLocked) scheduleReveal() else scheduleTimer()
    }

    private fun scheduleTimer() {
        val remaining = _uiState.value.remainingTimerMs ?: return
        if (_uiState.value.isPaused || _uiState.value.answerLocked || _uiState.value.advice != null) return
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var left = remaining
            while (left > 0L) {
                val step = minOf(left, 100L)
                delay(step)
                left -= step
                _uiState.update { it.copy(remainingTimerMs = left) }
            }
            submitAnswerOrTimeout(null)
        }
    }

    private fun scheduleReveal() {
        if (_uiState.value.isPaused) return
        revealJob?.cancel()
        revealJob = viewModelScope.launch {
            while (revealRemainingMs > 0L) {
                val step = minOf(revealRemainingMs, 100L)
                delay(step)
                revealRemainingMs -= step
            }
            val session = _uiState.value.session ?: return@launch
            val progressed = advanceTurnUseCase(session)

            if (progressed.status == GameStatus.FINISHED) {
                val gameResult = finishGameUseCase(progressed)
                getNextQuestionUseCase.recordCompletedGame()
                _uiState.value = GameUiState(
                    session = progressed,
                    remainingOptionIds = null,
                    result = gameResult
                )
                _audioEvents.tryEmit(SoundEffect.GAME_COMPLETE)
                persistCompletedGame(progressed, gameResult)
                return@launch
            }

            val next = getNextQuestionUseCase(progressed)
            if (next.question == null || next.updatedSession.status == GameStatus.FINISHED) {
                val gameResult = finishGameUseCase(next.updatedSession)
                getNextQuestionUseCase.recordCompletedGame()
                _uiState.value = GameUiState(
                    session = next.updatedSession,
                    remainingOptionIds = null,
                    result = gameResult
                )
                _audioEvents.tryEmit(SoundEffect.GAME_COMPLETE)
                persistCompletedGame(next.updatedSession, gameResult)
                return@launch
            }

            _uiState.value = GameUiState(
                session = next.updatedSession,
                currentQuestion = next.question,
                timerEnabled = _uiState.value.timerEnabled,
                remainingTimerMs = if (_uiState.value.timerEnabled) questionTimeLimitMs else null
            )
            scheduleTimer()
        }
    }

    fun restartGame() {
        timerJob?.cancel()
        revealJob?.cancel()
        _uiState.value = GameUiState()
    }

    private fun persistCompletedGame(
        session: GameSession,
        result: GameResult
    ) {
        viewModelScope.launch {
            saveCompletedGameUseCase(
                session = session,
                result = result
            )
        }
    }
}
