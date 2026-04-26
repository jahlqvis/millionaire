package se.yourcompany.miljonaren.data.repository

import se.yourcompany.miljonaren.data.local.content.SwedishQuestionSeed
import se.yourcompany.miljonaren.domain.model.Question
import se.yourcompany.miljonaren.domain.usecase.QuestionRepository

class LocalQuestionRepository(
    private val allQuestions: List<Question> = SwedishQuestionSeed.questions
) : QuestionRepository {

    override fun getNextUnusedQuestion(askedIds: Set<String>): Question? {
        return allQuestions.firstOrNull { question -> question.id !in askedIds }
    }
}
