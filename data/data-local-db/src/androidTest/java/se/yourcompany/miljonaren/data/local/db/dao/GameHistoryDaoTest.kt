package se.yourcompany.miljonaren.data.local.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import se.yourcompany.miljonaren.data.local.db.MillionaireDatabase
import se.yourcompany.miljonaren.data.local.db.entity.GameSessionEntity
import se.yourcompany.miljonaren.data.local.db.entity.PlayerResultEntity

@RunWith(AndroidJUnit4::class)
class GameHistoryDaoTest {

    private lateinit var database: MillionaireDatabase
    private lateinit var dao: GameHistoryDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MillionaireDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.gameHistoryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertCompletedGame_savesSessionAndPlayerRows() = runTest {
        val session = GameSessionEntity(
            sessionId = "s1",
            completedAtEpochMs = 10_000L,
            totalPlayers = 2,
            totalRounds = 5,
            isTie = false,
            winnerName = "Anna"
        )
        val playerResults = listOf(
            PlayerResultEntity("s1", "p1", "Anna", 1, 4),
            PlayerResultEntity("s1", "p2", "Bengt", 2, 2)
        )

        dao.insertCompletedGame(session, playerResults)

        val sessions = dao.getRecentGameSessions()
        val results = dao.getPlayerResultsForSession("s1")

        assertEquals(1, sessions.size)
        assertEquals("s1", sessions.first().sessionId)
        assertEquals(2, results.size)
        assertEquals(listOf(1, 2), results.map { it.placement })
    }

    @Test
    fun getRecentGameSessions_returnsNewestFirst() = runTest {
        dao.insertCompletedGame(
            gameSession = GameSessionEntity("old", 1_000L, 2, 5, false, "Anna"),
            playerResults = listOf(
                PlayerResultEntity("old", "p1", "Anna", 1, 3)
            )
        )
        dao.insertCompletedGame(
            gameSession = GameSessionEntity("new", 9_000L, 2, 5, true, null),
            playerResults = listOf(
                PlayerResultEntity("new", "p1", "Anna", 1, 3)
            )
        )

        val sessions = dao.getRecentGameSessions()

        assertEquals(listOf("new", "old"), sessions.map { it.sessionId })
    }

    @Test
    fun getPlayerResultsForSession_returnsPlacementOrder() = runTest {
        dao.insertCompletedGame(
            gameSession = GameSessionEntity("s1", 1_000L, 3, 5, false, "Carl"),
            playerResults = listOf(
                PlayerResultEntity("s1", "p2", "Bengt", 2, 3),
                PlayerResultEntity("s1", "p3", "Carl", 1, 5),
                PlayerResultEntity("s1", "p1", "Anna", 3, 1)
            )
        )

        val results = dao.getPlayerResultsForSession("s1")

        assertEquals(listOf(1, 2, 3), results.map { it.placement })
        assertTrue(results.first().score >= results.last().score)
    }
}
