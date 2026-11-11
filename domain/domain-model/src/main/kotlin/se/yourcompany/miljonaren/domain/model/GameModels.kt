package se.yourcompany.miljonaren.domain.model

data class Player(
    val id: String,
    val name: String,
    val score: Int = 0
)

enum class LifelineType {
    FIFTY_FIFTY,
    AUDIENCE,
    PHONE_A_FRIEND
}

data class AnswerOption(
    val id: String,
    val textSv: String
)

data class QuestionTheme(val id: String, val nameSv: String)

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

data class TieBreakState(
    val round: Int,
    val remainingPlayerIds: List<String>,
    val correctPlayerIds: Set<String> = emptySet()
)

data class GameSession(
    val id: String,
    val players: List<Player>,
    val currentPlayerIndex: Int,
    val currentRound: Int,
    val maxRounds: Int,
    val askedQuestionIds: Set<String>,
    val currentQuestionId: String?,
    val usedLifelinesByPlayer: Map<String, Set<LifelineType>> = emptyMap(),
    val status: GameStatus,
    val tieBreak: TieBreakState? = null
) {
    val activePlayer: Player
        get() = players[currentPlayerIndex]

    fun hasUsedLifeline(playerId: String, lifelineType: LifelineType): Boolean {
        return lifelineType in usedLifelinesByPlayer[playerId].orEmpty()
    }

    fun markLifelineUsed(playerId: String, lifelineType: LifelineType): GameSession {
        val updatedLifelines = usedLifelinesByPlayer.toMutableMap()
        val currentUsage = updatedLifelines[playerId].orEmpty()
        updatedLifelines[playerId] = currentUsage + lifelineType
        return copy(usedLifelinesByPlayer = updatedLifelines)
    }
}

data class AnswerEvaluation(
    val updatedSession: GameSession,
    val isCorrect: Boolean
)

data class GameResult(
    val rankedPlayers: List<Player>,
    val isTie: Boolean,
    val winner: Player?,
    val winners: List<Player> = winner?.let { listOf(it) }
        ?: rankedPlayers.filter { it.score == rankedPlayers.firstOrNull()?.score }
) {
    fun placementOf(player: Player): Int =
        if (winners.any { it.id == player.id }) 1 else rankedPlayers.indexOfFirst { it.id == player.id } + 1
}
