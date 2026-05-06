package se.yourcompany.miljonaren.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import se.yourcompany.miljonaren.data.local.db.dao.GameHistoryDao
import se.yourcompany.miljonaren.data.local.db.entity.GameSessionEntity
import se.yourcompany.miljonaren.data.local.db.entity.PlayerResultEntity
import se.yourcompany.miljonaren.domain.model.GameResult
import se.yourcompany.miljonaren.domain.model.GameSession
import se.yourcompany.miljonaren.domain.model.GameStatus
import se.yourcompany.miljonaren.domain.model.Player

class RoomGameHistoryRepositoryTest {

    @Test
    fun saveCompletedGame_mapsSessionAndPlayerResults() = runTest {
        val dao = FakeGameHistoryDao()
        val repository = RoomGameHistoryRepository(dao)
        val session = sampleSession()
        val result = sampleResult()

        repository.saveCompletedGame(
            session = session,
            result = result,
            completedAtEpochMs = 123_000L
        )

        val savedSession = dao.gameSessions.single()
        val savedResults = dao.playerResultsBySession[session.id].orEmpty()

        assertEquals(session.id, savedSession.sessionId)
        assertEquals(123_000L, savedSession.completedAtEpochMs)
        assertEquals(2, savedSession.totalPlayers)
        assertEquals("Anna", savedSession.winnerName)
        assertEquals(listOf(1, 2), savedResults.map { it.placement })
        assertEquals(listOf("p1", "p2"), savedResults.map { it.playerId })
    }

    @Test
    fun getRecentGames_mapsEntitiesToDomainEntries() = runTest {
        val dao = FakeGameHistoryDao()
        dao.gameSessions += GameSessionEntity("s2", 2_000L, 2, 5, true, null)
        dao.gameSessions += GameSessionEntity("s1", 1_000L, 3, 5, false, "Anna")

        val repository = RoomGameHistoryRepository(dao)
        val entries = repository.getRecentGames()

        assertEquals(2, entries.size)
        assertEquals("s2", entries[0].sessionId)
        assertEquals(true, entries[0].isTie)
        assertNull(entries[0].winnerName)
        assertEquals("s1", entries[1].sessionId)
        assertEquals("Anna", entries[1].winnerName)
    }

    @Test
    fun getResultsForSession_mapsEntitiesToDomainResults() = runTest {
        val dao = FakeGameHistoryDao()
        dao.playerResultsBySession["s1"] = listOf(
            PlayerResultEntity("s1", "p1", "Anna", 1, 4),
            PlayerResultEntity("s1", "p2", "Bengt", 2, 2)
        )

        val repository = RoomGameHistoryRepository(dao)
        val results = repository.getResultsForSession("s1")

        assertEquals(2, results.size)
        assertEquals("Anna", results[0].playerName)
        assertEquals(1, results[0].placement)
        assertEquals("Bengt", results[1].playerName)
    }

    private fun sampleSession(): GameSession = GameSession(
        id = "session-1",
        players = listOf(
            Player(id = "p1", name = "Anna", score = 4),
            Player(id = "p2", name = "Bengt", score = 2)
        ),
        currentPlayerIndex = 0,
        currentRound = 5,
        maxRounds = 5,
        askedQuestionIds = emptySet(),
        currentQuestionId = null,
        usedLifelinesByPlayer = emptyMap(),
        status = GameStatus.FINISHED
    )

    private fun sampleResult(): GameResult = GameResult(
        rankedPlayers = listOf(
            Player(id = "p1", name = "Anna", score = 4),
            Player(id = "p2", name = "Bengt", score = 2)
        ),
        isTie = false,
        winner = Player(id = "p1", name = "Anna", score = 4)
    )

    private class FakeGameHistoryDao : GameHistoryDao {
        val gameSessions = mutableListOf<GameSessionEntity>()
        val playerResultsBySession = mutableMapOf<String, List<PlayerResultEntity>>()

        override suspend fun insertGameSession(gameSession: GameSessionEntity) {
            gameSessions.removeAll { it.sessionId == gameSession.sessionId }
            gameSessions += gameSession
            gameSessions.sortByDescending { it.completedAtEpochMs }
        }

        override suspend fun insertPlayerResults(playerResults: List<PlayerResultEntity>) {
            if (playerResults.isEmpty()) return
            playerResultsBySession[playerResults.first().sessionId] = playerResults
        }

        override suspend fun getRecentGameSessions(): List<GameSessionEntity> {
            return gameSessions.toList()
        }

        override suspend fun getPlayerResultsForSession(sessionId: String): List<PlayerResultEntity> {
            return playerResultsBySession[sessionId].orEmpty().sortedBy { it.placement }
        }
    }
}
