package se.yourcompany.miljonaren.tv.game

import se.yourcompany.miljonaren.domain.model.GameResult
import se.yourcompany.miljonaren.domain.model.GameSession
import se.yourcompany.miljonaren.domain.model.Question

data class GameUiState(
    val session: GameSession? = null,
    val currentQuestion: Question? = null,
    val answerFeedback: String? = null,
    val answerLocked: Boolean = false,
    val result: GameResult? = null
)
