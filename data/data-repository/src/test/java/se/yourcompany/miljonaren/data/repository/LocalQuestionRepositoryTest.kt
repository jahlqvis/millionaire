package se.yourcompany.miljonaren.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random
import se.yourcompany.miljonaren.data.local.content.BundledQuestionPack
import se.yourcompany.miljonaren.domain.model.AnswerOption
import se.yourcompany.miljonaren.domain.model.Difficulty
import se.yourcompany.miljonaren.domain.model.Question
import se.yourcompany.miljonaren.domain.model.QuestionTheme
import se.yourcompany.miljonaren.domain.usecase.StartGameUseCase

class LocalQuestionRepositoryTest {
    private fun question(id: String, difficulty: Difficulty) = Question(
        id = id,
        textSv = "Fråga $id",
        options = listOf("A", "B", "C", "D").map { AnswerOption(it, it) },
        correctOptionId = "A",
        category = "Test",
        difficulty = difficulty
    )

    @Test
    fun exhaustedPool_reusesLeastRecentlyUsedHardestQuestions() {
        val repository = LocalQuestionRepository(
            listOf(question("easy", Difficulty.EASY), question("hard1", Difficulty.HARD), question("hard2", Difficulty.HARD))
        )
        val asked = mutableSetOf<String>()
        val selected = mutableListOf<String>()
        repeat(3) {
            val id = repository.getNextUnusedQuestion(asked)!!.id
            selected += id
            asked += id
        }
        val hard = selected.filter { it.startsWith("hard") }

        assertEquals(hard[0], repository.getNextUnusedQuestion(asked)?.id)
        assertEquals(hard[1], repository.getNextUnusedQuestion(asked)?.id)
        assertEquals(hard[0], repository.getNextUnusedQuestion(asked)?.id)
    }

    @Test
    fun emptyPool_returnsNull() {
        assertNull(LocalQuestionRepository(emptyList()).getNextUnusedQuestion(emptySet()))
    }

    @Test
    fun completedGames_areRememberedAcrossRepositoriesButInterruptedGamesAreNot() {
        val store = InMemoryQuestionUsageStore()
        val pool = (1..3).map { question("q$it", Difficulty.EASY) }
        val first = LocalQuestionRepository(pool, store, Random(1))
        val abandoned = first.getNextUnusedQuestion(emptySet())!!.id
        first.startGame()
        assertTrue(store.load().isEmpty())
        val completed = first.getNextUnusedQuestion(emptySet())!!.id
        first.recordCompletedGame()
        assertEquals(listOf(listOf("general::$completed")), store.load())
        val second = LocalQuestionRepository(pool, store, Random(2))
        assertTrue(second.getNextUnusedQuestion(emptySet())!!.id != completed)
        assertTrue(abandoned in pool.map { it.id })
    }

    @Test
    fun completedHistory_retainsOnlyTenGames() {
        val store = InMemoryQuestionUsageStore()
        val repository = LocalQuestionRepository(listOf(question("only", Difficulty.EASY)), store, Random(1))
        repeat(12) {
            repository.startGame()
            repository.getNextUnusedQuestion(emptySet())
            repository.recordCompletedGame()
        }
        assertEquals(10, store.load().size)
    }

    @Test
    fun sameRound_prefersComparableDifficulty() {
        val pool = listOf(
            question("e1", Difficulty.EASY), question("e2", Difficulty.EASY),
            question("h1", Difficulty.HARD), question("h2", Difficulty.HARD)
        )
        val repository = LocalQuestionRepository(pool, random = Random(5))
        val session = StartGameUseCase()(listOf("Anna", "Bengt"))
        val first = repository.getNextQuestion(session)!!
        val second = repository.getNextQuestion(session.copy(askedQuestionIds = setOf(first.id), currentPlayerIndex = 1))!!
        assertEquals(first.difficulty, second.difficulty)
        assertTrue(first.id != second.id)
    }

    @Test
    fun themeSelection_keepsQuestionsAndUsageSeparate() {
        val store = InMemoryQuestionUsageStore()
        store.save(listOf(listOf("general::q1", "general::q2")))
        val zeroRandom = object : Random() { override fun nextBits(bitCount: Int) = 0 }
        val repository = LocalQuestionRepository(
            usageStore = store,
            random = zeroRandom,
            packs = listOf(
                BundledQuestionPack(QuestionTheme("general", "Blandat"), listOf(question("q1", Difficulty.EASY))),
                BundledQuestionPack(QuestionTheme("space", "Rymden"), listOf(question("q1", Difficulty.EASY), question("q2", Difficulty.HARD)))
            )
        )
        assertEquals(listOf("general", "space"), repository.getQuestionThemes().map { it.id })
        repository.selectQuestionTheme("space")
        repository.startGame()
        assertEquals("q1", repository.getNextUnusedQuestion(emptySet())?.id)
        repository.recordCompletedGame()
        assertEquals(listOf("space::q1"), store.load().last())
    }

    @Test
    fun legacyQuestionUsage_stillAppliesToDefaultPack() {
        val store = InMemoryQuestionUsageStore()
        store.save(listOf(listOf("q1")))
        val repository = LocalQuestionRepository(
            listOf(question("q1", Difficulty.EASY), question("q2", Difficulty.EASY)), store, Random(1)
        )
        assertEquals("q2", repository.getNextUnusedQuestion(emptySet())?.id)
        org.junit.Assert.assertThrows(IllegalArgumentException::class.java) {
            repository.selectQuestionTheme("unknown")
        }
    }
}
