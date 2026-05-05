package se.yourcompany.miljonaren.data.repository

import se.yourcompany.miljonaren.data.local.db.dao.GameHistoryDao
import se.yourcompany.miljonaren.data.local.db.entity.GameSessionEntity
import se.yourcompany.miljonaren.data.local.db.entity.PlayerResultEntity
import se.yourcompany.miljonaren.domain.model.GameHistoryEntry
import se.yourcompany.miljonaren.domain.model.GameResult
import se.yourcompany.miljonaren.domain.model.GameSession
import se.yourcompany.miljonaren.domain.model.PlayerHistoryResult
import se.yourcompany.miljonaren.domain.usecase.GameHistoryRepository

class RoomGameHistoryRepository(
    private val gameHistoryDao: GameHistoryDao
) : GameHistoryRepository {

    override suspend fun saveCompletedGame(
        session: GameSession,
        result: GameResult,
        completedAtEpochMs: Long
    ) {
        val gameSessionEntity = GameSessionEntity(
            sessionId = session.id,
            completedAtEpochMs = completedAtEpochMs,
            totalPlayers = session.players.size,
            totalRounds = session.maxRounds,
            isTie = result.isTie,
            winnerName = result.winner?.name
        )

        val playerResults = result.rankedPlayers.mapIndexed { index, player ->
            PlayerResultEntity(
                sessionId = session.id,
                playerId = player.id,
                playerName = player.name,
                placement = index + 1,
                score = player.score
            )
        }

        gameHistoryDao.insertCompletedGame(
            gameSession = gameSessionEntity,
            playerResults = playerResults
        )
    }

    override suspend fun getRecentGames(): List<GameHistoryEntry> {
        return gameHistoryDao.getRecentGameSessions().map { entity ->
            GameHistoryEntry(
                sessionId = entity.sessionId,
                completedAtEpochMs = entity.completedAtEpochMs,
                totalPlayers = entity.totalPlayers,
                totalRounds = entity.totalRounds,
                isTie = entity.isTie,
                winnerName = entity.winnerName
            )
        }
    }

    override suspend fun getResultsForSession(sessionId: String): List<PlayerHistoryResult> {
        return gameHistoryDao.getPlayerResultsForSession(sessionId).map { entity ->
            PlayerHistoryResult(
                sessionId = entity.sessionId,
                playerId = entity.playerId,
                playerName = entity.playerName,
                placement = entity.placement,
                score = entity.score
            )
        }
    }
}
