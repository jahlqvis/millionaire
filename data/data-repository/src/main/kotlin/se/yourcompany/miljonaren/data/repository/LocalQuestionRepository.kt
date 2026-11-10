package se.yourcompany.miljonaren.data.repository

import se.yourcompany.miljonaren.data.local.content.SwedishQuestionSeed
import se.yourcompany.miljonaren.domain.model.Question
import se.yourcompany.miljonaren.domain.usecase.QuestionRepository

class LocalQuestionRepository(
    private val allQuestions: List<Question> = SwedishQuestionSeed.questions
) : QuestionRepository {
    private val lastUsed = mutableMapOf<String, Long>()
    private var sequence = 0L

    override fun getNextUnusedQuestion(askedIds: Set<String>): Question? {
        val question = allQuestions.firstOrNull { it.id !in askedIds }
            ?: allQuestions.maxOfOrNull { it.difficulty.ordinal }?.let { hardest ->
                allQuestions.filter { it.difficulty.ordinal == hardest }
                    .minByOrNull { lastUsed[it.id] ?: Long.MIN_VALUE }
            }
        if (question != null) {
            lastUsed[question.id] = sequence++
        }
        return question
    }
}
