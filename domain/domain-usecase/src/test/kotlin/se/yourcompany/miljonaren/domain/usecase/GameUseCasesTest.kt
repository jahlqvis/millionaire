package se.yourcompany.miljonaren.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import se.yourcompany.miljonaren.domain.model.AnswerOption
import se.yourcompany.miljonaren.domain.model.Difficulty
import se.yourcompany.miljonaren.domain.model.GameSession
import se.yourcompany.miljonaren.domain.model.GameStatus
import se.yourcompany.miljonaren.domain.model.Player
import se.yourcompany.miljonaren.domain.model.Question

class GameUseCasesTest {

    @Test
    fun startGame_usesFiveRoundsAndFirstPlayerTurn() {
        val session = StartGameUseCase()(listOf("Anna", "Bengt"))

        assertEquals(2, session.players.size)
        assertEquals(0, session.currentPlayerIndex)
        assertEquals(1, session.currentRound)
        assertEquals(5, session.maxRounds)
        assertEquals(GameStatus.IN_PROGRESS, session.status)
    }

    @Test
    fun advanceTurn_rotatesPlayersAndIncrementsRounds() {
        val useCase = AdvanceTurnUseCase()
        val initial = sampleSession()

        val afterFirst = useCase(initial)
        assertEquals(1, afterFirst.currentPlayerIndex)
        assertEquals(1, afterFirst.currentRound)

        val afterSecond = useCase(afterFirst)
        assertEquals(0, afterSecond.currentPlayerIndex)
        assertEquals(2, afterSecond.currentRound)
    }

    @Test
    fun submitAnswer_addsScoreOnlyOnCorrectAnswer() {
        val useCase = SubmitAnswerUseCase()
        val session = sampleSession()
        val question = sampleQuestion(correctId = "B")

        val wrong = useCase(session, question, selectedOptionId = "A")
        assertFalse(wrong.isCorrect)
        assertEquals(0, wrong.updatedSession.players[0].score)

        val correct = useCase(session, question, selectedOptionId = "B")
        assertTrue(correct.isCorrect)
        assertEquals(1, correct.updatedSession.players[0].score)
    }

    @Test
    fun finishGame_returnsTieWhenTopScoresEqual() {
        val session = sampleSession(
            players = listOf(
                Player(id = "p1", name = "Anna", score = 3),
                Player(id = "p2", name = "Bengt", score = 3)
            ),
            status = GameStatus.FINISHED
        )

        val result = FinishGameUseCase()(session)
        assertTrue(result.isTie)
        assertNull(result.winner)
        assertEquals(2, result.rankedPlayers.size)
    }

    @Test
    fun getNextQuestion_marksAskedAndFinishesWhenEmpty() {
        val oneQuestion = sampleQuestion(id = "q1", correctId = "A")
        val repository = object : QuestionRepository {
            override fun getNextUnusedQuestion(askedIds: Set<String>): Question? {
                return if (oneQuestion.id in askedIds) null else oneQuestion
            }
        }

        val useCase = GetNextQuestionUseCase(repository)
        val session = sampleSession(askedQuestionIds = emptySet())

        val first = useCase(session)
        assertEquals("q1", first.question?.id)
        assertTrue("q1" in first.updatedSession.askedQuestionIds)

        val second = useCase(first.updatedSession)
        assertNull(second.question)
        assertEquals(GameStatus.FINISHED, second.updatedSession.status)
    }

    private fun sampleSession(
        players: List<Player> = listOf(
            Player(id = "p1", name = "Anna", score = 0),
            Player(id = "p2", name = "Bengt", score = 0)
        ),
        currentPlayerIndex: Int = 0,
        currentRound: Int = 1,
        askedQuestionIds: Set<String> = emptySet(),
        status: GameStatus = GameStatus.IN_PROGRESS
    ): GameSession = GameSession(
        id = "session-1",
        players = players,
        currentPlayerIndex = currentPlayerIndex,
        currentRound = currentRound,
        maxRounds = 5,
        askedQuestionIds = askedQuestionIds,
        currentQuestionId = null,
        status = status
    )

    private fun sampleQuestion(id: String = "question-1", correctId: String): Question = Question(
        id = id,
        textSv = "Testfraga",
        options = listOf(
            AnswerOption("A", "Alt A"),
            AnswerOption("B", "Alt B"),
            AnswerOption("C", "Alt C"),
            AnswerOption("D", "Alt D")
        ),
        correctOptionId = correctId,
        category = "Test",
        difficulty = Difficulty.EASY
    )
}
