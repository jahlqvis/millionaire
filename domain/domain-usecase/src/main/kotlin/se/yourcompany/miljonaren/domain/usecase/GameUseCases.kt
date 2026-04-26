package se.yourcompany.miljonaren.domain.usecase

import java.util.UUID
import se.yourcompany.miljonaren.domain.model.AnswerEvaluation
import se.yourcompany.miljonaren.domain.model.GameResult
import se.yourcompany.miljonaren.domain.model.GameSession
import se.yourcompany.miljonaren.domain.model.GameStatus
import se.yourcompany.miljonaren.domain.model.Player
import se.yourcompany.miljonaren.domain.model.Question

const val SPRINT_ONE_MAX_ROUNDS = 5
private const val MIN_PLAYERS = 1
private const val MAX_PLAYERS = 4

interface QuestionRepository {
    fun getNextUnusedQuestion(askedIds: Set<String>): Question?
}

data class NextQuestionResult(
    val updatedSession: GameSession,
    val question: Question?
)

class StartGameUseCase {
    operator fun invoke(playerNames: List<String>): GameSession {
        require(playerNames.size in MIN_PLAYERS..MAX_PLAYERS) {
            "Player count must be between $MIN_PLAYERS and $MAX_PLAYERS"
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
            maxRounds = SPRINT_ONE_MAX_ROUNDS,
            askedQuestionIds = emptySet(),
            currentQuestionId = null,
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

class SubmitAnswerUseCase {
    operator fun invoke(
        session: GameSession,
        question: Question,
        selectedOptionId: String
    ): AnswerEvaluation {
        if (session.status == GameStatus.FINISHED) {
            return AnswerEvaluation(session, isCorrect = false)
        }

        val isCorrect = question.correctOptionId == selectedOptionId
        val activeIndex = session.currentPlayerIndex
        val updatedPlayers = session.players.mapIndexed { index, player ->
            if (index == activeIndex && isCorrect) {
                player.copy(score = player.score + 1)
            } else {
                player
            }
        }

        return AnswerEvaluation(
            updatedSession = session.copy(
                players = updatedPlayers,
                currentQuestionId = null
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

        val lastPlayerIndex = session.players.lastIndex
        val isLastPlayerInRound = session.currentPlayerIndex == lastPlayerIndex

        if (!isLastPlayerInRound) {
            return session.copy(currentPlayerIndex = session.currentPlayerIndex + 1)
        }

        if (session.currentRound >= session.maxRounds) {
            return session.copy(status = GameStatus.FINISHED)
        }

        return session.copy(
            currentPlayerIndex = 0,
            currentRound = session.currentRound + 1
        )
    }
}

class FinishGameUseCase {
    operator fun invoke(session: GameSession): GameResult {
        val rankedPlayers = session.players.sortedWith(
            compareByDescending<Player> { it.score }.thenBy { it.name.lowercase() }
        )

        val topScore = rankedPlayers.firstOrNull()?.score
        val tiedPlayers = rankedPlayers.count { it.score == topScore }
        val isTie = tiedPlayers > 1

        return GameResult(
            rankedPlayers = rankedPlayers,
            isTie = isTie,
            winner = if (isTie) null else rankedPlayers.firstOrNull()
        )
    }
}
