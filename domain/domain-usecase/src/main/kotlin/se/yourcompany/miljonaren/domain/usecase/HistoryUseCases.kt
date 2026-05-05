package se.yourcompany.miljonaren.domain.usecase

import se.yourcompany.miljonaren.domain.model.GameHistoryEntry
import se.yourcompany.miljonaren.domain.model.GameResult
import se.yourcompany.miljonaren.domain.model.GameSession
import se.yourcompany.miljonaren.domain.model.PlayerHistoryResult

interface GameHistoryRepository {
    suspend fun saveCompletedGame(
        session: GameSession,
        result: GameResult,
        completedAtEpochMs: Long
    )

    suspend fun getRecentGames(): List<GameHistoryEntry>

    suspend fun getResultsForSession(sessionId: String): List<PlayerHistoryResult>
}

class SaveCompletedGameUseCase(
    private val gameHistoryRepository: GameHistoryRepository
) {
    suspend operator fun invoke(
        session: GameSession,
        result: GameResult,
        completedAtEpochMs: Long = System.currentTimeMillis()
    ) {
        gameHistoryRepository.saveCompletedGame(
            session = session,
            result = result,
            completedAtEpochMs = completedAtEpochMs
        )
    }
}

class GetRecentGamesUseCase(
    private val gameHistoryRepository: GameHistoryRepository
) {
    suspend operator fun invoke(): List<GameHistoryEntry> {
        return gameHistoryRepository.getRecentGames()
    }
}

class GetGameResultsUseCase(
    private val gameHistoryRepository: GameHistoryRepository
) {
    suspend operator fun invoke(sessionId: String): List<PlayerHistoryResult> {
        return gameHistoryRepository.getResultsForSession(sessionId)
    }
}

class NoOpGameHistoryRepository : GameHistoryRepository {
    override suspend fun saveCompletedGame(
        session: GameSession,
        result: GameResult,
        completedAtEpochMs: Long
    ) {
    }

    override suspend fun getRecentGames(): List<GameHistoryEntry> = emptyList()

    override suspend fun getResultsForSession(sessionId: String): List<PlayerHistoryResult> = emptyList()
}
