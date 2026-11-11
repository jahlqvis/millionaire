package se.yourcompany.miljonaren.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import se.yourcompany.miljonaren.domain.model.AnswerOption
import se.yourcompany.miljonaren.domain.model.Difficulty
import se.yourcompany.miljonaren.domain.model.Question

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
        repeat(3) { asked += repository.getNextUnusedQuestion(asked)!!.id }

        assertEquals("hard1", repository.getNextUnusedQuestion(asked)?.id)
        assertEquals("hard2", repository.getNextUnusedQuestion(asked)?.id)
        assertEquals("hard1", repository.getNextUnusedQuestion(asked)?.id)
    }

    @Test
    fun emptyPool_returnsNull() {
        assertNull(LocalQuestionRepository(emptyList()).getNextUnusedQuestion(emptySet()))
    }
}
