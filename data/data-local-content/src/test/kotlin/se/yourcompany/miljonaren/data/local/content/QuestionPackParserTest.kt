package se.yourcompany.miljonaren.data.local.content

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import se.yourcompany.miljonaren.domain.model.Difficulty

class QuestionPackParserTest {
    private val question = """{"id":"q001","textSv":"Fråga?","category":"Test","difficulty":"EASY","options":[{"id":"A","textSv":"Ett"},{"id":"B","textSv":"Två"},{"id":"C","textSv":"Tre"},{"id":"D","textSv":"Fyra"}],"correctOptionId":"B"}"""
    private fun pack(vararg questions: String) = """{"version":1,"questions":[${questions.joinToString(",")}]}"""

    @Test
    fun bundledPack_isValidAndAvailable() {
        val questions = SwedishQuestionSeed.questions
        assertTrue(questions.isNotEmpty())
        assertEquals("q001", questions.first().id)
        assertEquals("Stockholm", questions.first().options.first().textSv)
    }

    @Test
    fun parse_readsValidQuestion() {
        val parsed = QuestionPackParser.parse(pack(question)).single()
        assertEquals(Difficulty.EASY, parsed.difficulty)
        assertEquals("B", parsed.correctOptionId)
        assertEquals(4, parsed.options.size)
    }

    @Test
    fun parse_rejectsDuplicateIdsAndTextsIgnoringCase() {
        val duplicateId = question.replace("Fråga?", "Annan fråga?")
        assertThrows(IllegalArgumentException::class.java) { QuestionPackParser.parse(pack(question, duplicateId)) }
        val duplicateText = question.replace("q001", "q002").replace("Fråga?", "FRÅGA?")
        assertThrows(IllegalArgumentException::class.java) { QuestionPackParser.parse(pack(question, duplicateText)) }
    }

    @Test
    fun parse_rejectsMissingInvalidAndDuplicateFields() {
        val invalid = listOf(
            question.replace("\"category\":\"Test\",", ""),
            question.replace("\"difficulty\":\"EASY\"", "\"difficulty\":\"UNKNOWN\""),
            question.replace("\"correctOptionId\":\"B\"", "\"correctOptionId\":\"E\""),
            question.replace("\"textSv\":\"Fyra\"", "\"textSv\":\"Ett\""),
            question.replace("\"id\":\"D\"", "\"id\":\"A\"")
        )
        invalid.forEach { input ->
            assertThrows(IllegalArgumentException::class.java) { QuestionPackParser.parse(pack(input)) }
        }
        assertThrows(IllegalArgumentException::class.java) { QuestionPackParser.parse("""{"version":2,"questions":[]}""") }
        assertThrows(IllegalArgumentException::class.java) { QuestionPackParser.parse(pack()) }
    }
}
