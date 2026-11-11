package se.yourcompany.miljonaren.tv.game

import se.yourcompany.miljonaren.domain.model.GameResult
import se.yourcompany.miljonaren.domain.model.GameSession
import se.yourcompany.miljonaren.domain.model.Question
import se.yourcompany.miljonaren.domain.usecase.LifelineAdvice

enum class AnswerFeedback {
    CORRECT,
    WRONG,
    TIMEOUT
}

data class GameUiState(
    val session: GameSession? = null,
    val currentQuestion: Question? = null,
    val remainingOptionIds: Set<String>? = null,
    val answerFeedback: AnswerFeedback? = null,
    val answerLocked: Boolean = false,
    val timerEnabled: Boolean = false,
    val remainingTimerMs: Long? = null,
    val isPaused: Boolean = false,
    val advice: LifelineAdvice? = null,
    val result: GameResult? = null
)
