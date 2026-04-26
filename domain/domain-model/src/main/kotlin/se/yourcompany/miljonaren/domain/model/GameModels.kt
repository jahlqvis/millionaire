package se.yourcompany.miljonaren.domain.model

data class Player(
    val id: String,
    val name: String,
    val score: Int = 0
)

data class AnswerOption(
    val id: String,
    val textSv: String
)

data class Question(
    val id: String,
    val textSv: String,
    val options: List<AnswerOption>,
    val correctOptionId: String,
    val category: String,
    val difficulty: Difficulty
) {
    init {
        require(options.size == 4) { "Each question must have exactly 4 options." }
        require(options.any { it.id == correctOptionId }) {
            "Correct option must exist in options."
        }
    }
}

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
}

enum class GameStatus {
    IN_PROGRESS,
    FINISHED
}

data class GameSession(
    val id: String,
    val players: List<Player>,
    val currentPlayerIndex: Int,
    val currentRound: Int,
    val maxRounds: Int,
    val askedQuestionIds: Set<String>,
    val currentQuestionId: String?,
    val status: GameStatus
) {
    val activePlayer: Player
        get() = players[currentPlayerIndex]
}

data class AnswerEvaluation(
    val updatedSession: GameSession,
    val isCorrect: Boolean
)

data class GameResult(
    val rankedPlayers: List<Player>,
    val isTie: Boolean,
    val winner: Player?
)
