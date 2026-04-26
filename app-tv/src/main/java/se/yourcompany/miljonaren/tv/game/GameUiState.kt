package se.yourcompany.miljonaren.tv.game

import se.yourcompany.miljonaren.domain.model.GameResult
import se.yourcompany.miljonaren.domain.model.GameSession
import se.yourcompany.miljonaren.domain.model.Question

enum class AnswerFeedback {
    CORRECT,
    WRONG
}

data class GameUiState(
    val session: GameSession? = null,
    val currentQuestion: Question? = null,
    val answerFeedback: AnswerFeedback? = null,
    val answerLocked: Boolean = false,
    val result: GameResult? = null
)
