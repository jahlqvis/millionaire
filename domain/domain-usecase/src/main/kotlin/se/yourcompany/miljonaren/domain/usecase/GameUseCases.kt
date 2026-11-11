package se.yourcompany.miljonaren.domain.usecase

import java.util.UUID
import kotlin.random.Random
import se.yourcompany.miljonaren.domain.model.AnswerEvaluation
import se.yourcompany.miljonaren.domain.model.GameResult
import se.yourcompany.miljonaren.domain.model.GameSession
import se.yourcompany.miljonaren.domain.model.GameStatus
import se.yourcompany.miljonaren.domain.model.LifelineType
import se.yourcompany.miljonaren.domain.model.Player
import se.yourcompany.miljonaren.domain.model.Question
import se.yourcompany.miljonaren.domain.model.TieBreakState

const val SPRINT_ONE_MAX_ROUNDS = 5
val SUPPORTED_ROUND_COUNTS = setOf(3, 5, 10)
private const val MIN_PLAYERS = 1
private const val MAX_PLAYERS = 4

interface QuestionRepository {
    fun getNextUnusedQuestion(askedIds: Set<String>): Question?
}

data class NextQuestionResult(
    val updatedSession: GameSession,
    val question: Question?
)

data class FiftyFiftyResult(
    val updatedSession: GameSession,
    val remainingOptionIds: Set<String>
)

data class LifelineAdvice(
    val type: LifelineType,
    val suggestedOptionId: String,
    val audiencePercentages: Map<String, Int>,
    val confidencePercent: Int = 80
)

data class AdviceResult(val updatedSession: GameSession, val advice: LifelineAdvice)

class GetLifelineAdviceUseCase(private val randomInt: (Int) -> Int = { Random.nextInt(it) }) {
    operator fun invoke(
        session: GameSession,
        question: Question,
        type: LifelineType,
        remainingOptionIds: Set<String>? = null
    ): AdviceResult {
        require(type == LifelineType.AUDIENCE || type == LifelineType.PHONE_A_FRIEND)
        require(session.status == GameStatus.IN_PROGRESS)
        require(session.currentQuestionId == question.id)
        require(!session.hasUsedLifeline(session.activePlayer.id, type))

        val available = question.options.filter { remainingOptionIds == null || it.id in remainingOptionIds }
        require(available.size == 2 || available.size == 4)
        require(available.any { it.id == question.correctOptionId })
        val incorrect = available.filter { it.id != question.correctOptionId }
        val suggestedId = if (randomInt(100) < 80) {
            question.correctOptionId
        } else {
            incorrect[randomInt(incorrect.size)].id
        }
        val ranked = listOf(suggestedId) + available.map { it.id }.filter { it != suggestedId }
        val shares = if (available.size == 2) listOf(70, 30) else listOf(55, 25, 12, 8)
        val percentages = question.options.associate { option ->
            option.id to ranked.indexOf(option.id).let { index -> if (index < 0) 0 else shares[index] }
        }
        return AdviceResult(
            updatedSession = session.markLifelineUsed(session.activePlayer.id, type),
            advice = LifelineAdvice(type, suggestedId, percentages)
        )
    }
}

class StartGameUseCase {
    operator fun invoke(playerNames: List<String>, maxRounds: Int = SPRINT_ONE_MAX_ROUNDS): GameSession {
        require(playerNames.size in MIN_PLAYERS..MAX_PLAYERS) {
            "Player count must be between $MIN_PLAYERS and $MAX_PLAYERS"
        }
        require(maxRounds in SUPPORTED_ROUND_COUNTS) {
            "Round count must be one of $SUPPORTED_ROUND_COUNTS"
        }

        val players = playerNames.mapIndexed { index, rawName ->
            val fallbackName = "Spelare ${index + 1}"
            Player(
                id = "p${index + 1}",
                name = rawName.trim().ifBlank { fallbackName }
            )
        }

        return GameSession(
            id = UUID.randomUUID().toString(),
            players = players,
            currentPlayerIndex = 0,
            currentRound = 1,
            maxRounds = maxRounds,
            askedQuestionIds = emptySet(),
            currentQuestionId = null,
            usedLifelinesByPlayer = emptyMap(),
            status = GameStatus.IN_PROGRESS
        )
    }
}

class GetNextQuestionUseCase(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(session: GameSession): NextQuestionResult {
        if (session.status == GameStatus.FINISHED) {
            return NextQuestionResult(session, null)
        }

        val nextQuestion = questionRepository.getNextUnusedQuestion(session.askedQuestionIds)
        if (nextQuestion == null) {
            return NextQuestionResult(session.copy(status = GameStatus.FINISHED), null)
        }

        return NextQuestionResult(
            updatedSession = session.copy(
                askedQuestionIds = session.askedQuestionIds + nextQuestion.id,
                currentQuestionId = nextQuestion.id
            ),
            question = nextQuestion
        )
    }
}

class ApplyFiftyFiftyUseCase {
    operator fun invoke(
        session: GameSession,
        question: Question
    ): FiftyFiftyResult {
        require(session.status == GameStatus.IN_PROGRESS) {
            "Cannot use lifelines when the game is finished."
        }
        require(session.currentQuestionId == question.id) {
            "50/50 can only be used on the active question."
        }
        require(!session.hasUsedLifeline(session.activePlayer.id, LifelineType.FIFTY_FIFTY)) {
            "50/50 has already been used by this player."
        }

        val incorrectOptionToKeep = question.options.firstOrNull { option ->
            option.id != question.correctOptionId
        } ?: error("Question must contain at least one incorrect option.")

        val remainingOptionIds = question.options
            .filter { option ->
                option.id == question.correctOptionId || option.id == incorrectOptionToKeep.id
            }
            .map { option -> option.id }
            .toSet()

        val updatedSession = session.markLifelineUsed(
            playerId = session.activePlayer.id,
            lifelineType = LifelineType.FIFTY_FIFTY
        )

        return FiftyFiftyResult(
            updatedSession = updatedSession,
            remainingOptionIds = remainingOptionIds
        )
    }
}

class SubmitAnswerUseCase {
    operator fun invoke(
        session: GameSession,
        question: Question,
        selectedOptionId: String?
    ): AnswerEvaluation {
        if (session.status == GameStatus.FINISHED) {
            return AnswerEvaluation(session, isCorrect = false)
        }

        val isCorrect = question.correctOptionId == selectedOptionId
        val activeIndex = session.currentPlayerIndex
        val updatedPlayers = session.players.mapIndexed { index, player ->
            if (session.tieBreak == null && index == activeIndex && isCorrect) {
                player.copy(score = player.score + 1)
            } else {
                player
            }
        }

        return AnswerEvaluation(
            updatedSession = session.copy(
                players = updatedPlayers,
                currentQuestionId = null,
                tieBreak = session.tieBreak?.let { tie ->
                    if (isCorrect) tie.copy(correctPlayerIds = tie.correctPlayerIds + session.activePlayer.id) else tie
                }
            ),
            isCorrect = isCorrect
        )
    }
}

class AdvanceTurnUseCase {
    operator fun invoke(session: GameSession): GameSession {
        if (session.status == GameStatus.FINISHED) {
            return session
        }

        val tie = session.tieBreak
        if (tie != null) {
            val playerPosition = tie.remainingPlayerIds.indexOf(session.activePlayer.id)
            require(playerPosition >= 0) { "Active player must be in the tie-breaker." }
            if (playerPosition < tie.remainingPlayerIds.lastIndex) {
                val nextId = tie.remainingPlayerIds[playerPosition + 1]
                return session.copy(currentPlayerIndex = session.players.indexOfFirst { it.id == nextId })
            }
            val survivors = if (tie.correctPlayerIds.isEmpty()) tie.remainingPlayerIds
                else tie.remainingPlayerIds.filter { it in tie.correctPlayerIds }
            if (survivors.size == 1 || tie.round >= 3) {
                return session.copy(
                    status = GameStatus.FINISHED,
                    tieBreak = tie.copy(remainingPlayerIds = survivors)
                )
            }
            return session.copy(
                currentPlayerIndex = session.players.indexOfFirst { it.id == survivors.first() },
                tieBreak = TieBreakState(round = tie.round + 1, remainingPlayerIds = survivors)
            )
        }

        val lastPlayerIndex = session.players.lastIndex
        val isLastPlayerInRound = session.currentPlayerIndex == lastPlayerIndex

        if (!isLastPlayerInRound) {
            return session.copy(currentPlayerIndex = session.currentPlayerIndex + 1)
        }

        if (session.currentRound >= session.maxRounds) {
            val topScore = session.players.maxOf { it.score }
            val leaders = session.players.filter { it.score == topScore }
            if (leaders.size < 2) return session.copy(status = GameStatus.FINISHED)
            return session.copy(
                currentPlayerIndex = session.players.indexOfFirst { it.id == leaders.first().id },
                tieBreak = TieBreakState(round = 1, remainingPlayerIds = leaders.map { it.id })
            )
        }

        return session.copy(
            currentPlayerIndex = 0,
            currentRound = session.currentRound + 1
        )
    }
}

class FinishGameUseCase {
    operator fun invoke(session: GameSession): GameResult {
        val topScore = session.players.maxOfOrNull { it.score }
        val winnerIds = session.tieBreak?.remainingPlayerIds?.toSet()
            ?: session.players.filter { it.score == topScore }.map { it.id }.toSet()
        val rankedPlayers = session.players.sortedWith(
            compareByDescending<Player> { it.id in winnerIds }
                .thenByDescending { it.score }
                .thenBy { it.name.lowercase() }
        )
        val winners = rankedPlayers.filter { it.id in winnerIds }
        val isTie = winners.size > 1

        return GameResult(
            rankedPlayers = rankedPlayers,
            isTie = isTie,
            winner = winners.singleOrNull(),
            winners = winners
        )
    }
}
