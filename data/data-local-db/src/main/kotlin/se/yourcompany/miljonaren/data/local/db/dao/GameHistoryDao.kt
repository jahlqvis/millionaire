package se.yourcompany.miljonaren.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import se.yourcompany.miljonaren.data.local.db.entity.GameSessionEntity
import se.yourcompany.miljonaren.data.local.db.entity.PlayerResultEntity

@Dao
interface GameHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameSession(gameSession: GameSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerResults(playerResults: List<PlayerResultEntity>)

    @Transaction
    suspend fun insertCompletedGame(
        gameSession: GameSessionEntity,
        playerResults: List<PlayerResultEntity>
    ) {
        insertGameSession(gameSession)
        insertPlayerResults(playerResults)
    }

    @Transaction
    suspend fun insertCompletedGameAndPrune(
        gameSession: GameSessionEntity,
        playerResults: List<PlayerResultEntity>,
        limit: Int
    ) {
        insertCompletedGame(gameSession, playerResults)
        pruneHistory(limit)
    }

    @Query("DELETE FROM game_sessions WHERE sessionId NOT IN (SELECT sessionId FROM game_sessions ORDER BY completedAtEpochMs DESC, sessionId DESC LIMIT :limit)")
    suspend fun pruneHistory(limit: Int)

    @Query("DELETE FROM game_sessions")
    suspend fun clearHistory()

    @Query("SELECT * FROM game_sessions ORDER BY completedAtEpochMs DESC")
    suspend fun getRecentGameSessions(): List<GameSessionEntity>

    @Query("SELECT * FROM player_results WHERE sessionId = :sessionId ORDER BY placement ASC")
    suspend fun getPlayerResultsForSession(sessionId: String): List<PlayerResultEntity>
}
