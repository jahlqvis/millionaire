package se.yourcompany.miljonaren.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import se.yourcompany.miljonaren.domain.model.AnswerOption
import se.yourcompany.miljonaren.domain.model.Difficulty
import se.yourcompany.miljonaren.domain.model.GameSession
import se.yourcompany.miljonaren.domain.model.GameStatus
import se.yourcompany.miljonaren.domain.model.LifelineType
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
    fun startGame_acceptsSupportedRoundCountsAndRejectsOthers() {
        for (rounds in listOf(3, 5, 10)) {
            assertEquals(rounds, StartGameUseCase()(listOf("Anna"), rounds).maxRounds)
        }
        assertThrows(IllegalArgumentException::class.java) {
            StartGameUseCase()(listOf("Anna"), 4)
        }
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

    @Test
    fun applyFiftyFifty_keepsCorrectAnswerAndOneIncorrectOption() {
        val useCase = ApplyFiftyFiftyUseCase()
        val question = sampleQuestion(id = "q1", correctId = "C")
        val session = sampleSession(currentQuestionId = question.id)

        val result = useCase(session, question)

        assertEquals(setOf("A", "C"), result.remainingOptionIds)
        assertTrue(
            result.updatedSession.hasUsedLifeline(
                playerId = session.activePlayer.id,
                lifelineType = LifelineType.FIFTY_FIFTY
            )
        )
    }

    @Test
    fun applyFiftyFifty_rejectsRepeatedUsageForSamePlayer() {
        val useCase = ApplyFiftyFiftyUseCase()
        val question = sampleQuestion(id = "q1", correctId = "A")
        val session = sampleSession(currentQuestionId = question.id)
            .markLifelineUsed("p1", LifelineType.FIFTY_FIFTY)

        assertThrows(IllegalArgumentException::class.java) {
            useCase(session, question)
        }
    }

    @Test
    fun applyFiftyFifty_rejectsFinishedGames() {
        val useCase = ApplyFiftyFiftyUseCase()
        val question = sampleQuestion(id = "q1", correctId = "A")
        val session = sampleSession(
            currentQuestionId = question.id,
            status = GameStatus.FINISHED
        )

        assertThrows(IllegalArgumentException::class.java) {
            useCase(session, question)
        }
    }

    @Test
    fun applyFiftyFifty_rejectsNonActiveQuestion() {
        val useCase = ApplyFiftyFiftyUseCase()
        val question = sampleQuestion(id = "q1", correctId = "A")
        val session = sampleSession(currentQuestionId = "other-question")

        assertThrows(IllegalArgumentException::class.java) {
            useCase(session, question)
        }
    }

    @Test
    fun advice_respectsFiftyFiftyAndShowsValidPercentages() {
        val question = sampleQuestion(id = "q1", correctId = "C")
        val session = sampleSession(currentQuestionId = question.id)
        val alwaysWrong = GetLifelineAdviceUseCase { bound -> if (bound == 100) 99 else 0 }

        for (type in listOf(LifelineType.AUDIENCE, LifelineType.PHONE_A_FRIEND)) {
            val result = alwaysWrong(session, question, type, setOf("A", "C"))
            assertEquals("A", result.advice.suggestedOptionId)
            assertEquals(100, result.advice.audiencePercentages.values.sum())
            assertEquals(70, result.advice.audiencePercentages["A"])
            assertEquals(30, result.advice.audiencePercentages["C"])
            assertEquals(0, result.advice.audiencePercentages["B"])
            assertEquals(0, result.advice.audiencePercentages["D"])
            assertTrue(result.updatedSession.hasUsedLifeline("p1", type))
            assertThrows(IllegalArgumentException::class.java) {
                alwaysWrong(result.updatedSession, question, type, setOf("A", "C"))
            }
        }
    }

    @Test
    fun advice_correctProbabilityUsesEightyOfOneHundredOutcomes() {
        val question = sampleQuestion(id = "q1", correctId = "C")
        val session = sampleSession(currentQuestionId = question.id)
        var draw = 0
        val advice = GetLifelineAdviceUseCase { bound -> if (bound == 100) draw++ else 0 }
        val correctCount = (0 until 100).count {
            advice(session, question, LifelineType.AUDIENCE).advice.suggestedOptionId == "C"
        }
        assertEquals(80, correctCount)
    }

    @Test
    fun advice_rejectsInactiveAndInvalidQuestions() {
        val question = sampleQuestion(id = "q1", correctId = "C")
        val useCase = GetLifelineAdviceUseCase { 0 }
        assertThrows(IllegalArgumentException::class.java) {
            useCase(sampleSession(currentQuestionId = "other"), question, LifelineType.AUDIENCE)
        }
        assertThrows(IllegalArgumentException::class.java) {
            useCase(sampleSession(currentQuestionId = "q1"), question, LifelineType.AUDIENCE, setOf("A", "B"))
        }
    }

    @Test
    fun tiedLeaders_enterTieBreakAndOnlyCorrectPlayerWinsWithoutChangingScores() {
        val players = listOf(Player("p1", "Anna", 2), Player("p2", "Bengt", 2), Player("p3", "Cecilia", 1))
        val advance = AdvanceTurnUseCase()
        val submit = SubmitAnswerUseCase()
        val question = sampleQuestion(id = "q1", correctId = "C")
        var session = advance(sampleSession(players = players, currentPlayerIndex = 2, currentRound = 5))
        assertEquals(listOf("p1", "p2"), session.tieBreak?.remainingPlayerIds)
        assertEquals("p1", session.activePlayer.id)

        session = advance(submit(session, question, "A").updatedSession)
        assertEquals("p2", session.activePlayer.id)
        session = advance(submit(session, question, "C").updatedSession)

        assertEquals(GameStatus.FINISHED, session.status)
        assertEquals(listOf(2, 2, 1), session.players.map { it.score })
        val result = FinishGameUseCase()(session)
        assertEquals("p2", result.winner?.id)
        assertEquals(listOf("p2", "p1", "p3"), result.rankedPlayers.map { it.id })
        assertEquals(listOf(1, 2, 3), result.rankedPlayers.map { result.placementOf(it) })
    }

    @Test
    fun tieBreak_allMissesPreservePlayersAndThreeRoundsEndInSharedWin() {
        val players = listOf(Player("p1", "Anna", 2), Player("p2", "Bengt", 2))
        val advance = AdvanceTurnUseCase()
        val submit = SubmitAnswerUseCase()
        val question = sampleQuestion(id = "q1", correctId = "C")
        var session = advance(sampleSession(players = players, currentPlayerIndex = 1, currentRound = 5))
        repeat(3) { round ->
            assertEquals(round + 1, session.tieBreak?.round)
            session = advance(submit(session, question, null).updatedSession)
            session = advance(submit(session, question, "A").updatedSession)
        }
        val result = FinishGameUseCase()(session)
        assertEquals(GameStatus.FINISHED, session.status)
        assertTrue(result.isTie)
        assertEquals(listOf("p1", "p2"), result.winners.map { it.id })
        assertEquals(listOf(1, 1), result.rankedPlayers.map { result.placementOf(it) })
    }

    @Test
    fun tieBreak_eliminatesOnlyAfterEveryRemainingPlayerHasAnswered() {
        val players = (1..4).map { Player("p$it", "Player $it", 3) }
        val advance = AdvanceTurnUseCase()
        val submit = SubmitAnswerUseCase()
        val question = sampleQuestion(id = "q1", correctId = "C")
        var session = advance(sampleSession(players = players, currentPlayerIndex = 3, currentRound = 5))
        for (answer in listOf("C", "A", "C", "A")) {
            session = advance(submit(session, question, answer).updatedSession)
        }
        assertEquals(GameStatus.IN_PROGRESS, session.status)
        assertEquals(2, session.tieBreak?.round)
        assertEquals(listOf("p1", "p3"), session.tieBreak?.remainingPlayerIds)
        assertEquals("p1", session.activePlayer.id)
    }

    private fun sampleSession(
        players: List<Player> = listOf(
            Player(id = "p1", name = "Anna", score = 0),
            Player(id = "p2", name = "Bengt", score = 0)
        ),
        currentPlayerIndex: Int = 0,
        currentRound: Int = 1,
        askedQuestionIds: Set<String> = emptySet(),
        currentQuestionId: String? = null,
        status: GameStatus = GameStatus.IN_PROGRESS
    ): GameSession = GameSession(
        id = "session-1",
        players = players,
        currentPlayerIndex = currentPlayerIndex,
        currentRound = currentRound,
        maxRounds = 5,
        askedQuestionIds = askedQuestionIds,
        currentQuestionId = currentQuestionId,
        usedLifelinesByPlayer = emptyMap(),
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
