package se.yourcompany.miljonaren.data.local.content

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.util.Locale
import se.yourcompany.miljonaren.domain.model.AnswerOption
import se.yourcompany.miljonaren.domain.model.Difficulty
import se.yourcompany.miljonaren.domain.model.Question

/** Parses and validates a version 1 bundled question pack. */
object QuestionPackParser {
    fun parse(json: String): List<Question> {
        val root = JsonParser.parseString(json)
        require(root.isJsonObject) { "Question pack must be an object" }
        val pack = root.asJsonObject
        val version = pack.get("version")
        require(version != null && version.isJsonPrimitive && version.asJsonPrimitive.isNumber && version.asString == "1") {
            "Unsupported question pack version"
        }
        val items = pack.get("questions")
        require(items != null && items.isJsonArray && items.asJsonArray.size() > 0) { "Question pack must contain questions" }
        val questions = items.asJsonArray.mapIndexed { index, item -> parseQuestion(item, index + 1) }
        require(questions.map { normalized(it.id) }.distinct().size == questions.size) { "Duplicate question ID" }
        require(questions.map { normalized(it.textSv) }.distinct().size == questions.size) { "Duplicate question text" }
        return questions
    }

    private fun parseQuestion(item: JsonElement, number: Int): Question {
        require(item.isJsonObject) { "Question $number must be an object" }
        val obj = item.asJsonObject
        val id = obj.requiredString("id")
        val text = obj.requiredString("textSv")
        val category = obj.requiredString("category")
        val difficulty = try {
            Difficulty.valueOf(obj.requiredString("difficulty"))
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Question $id has an invalid difficulty", e)
        }
        val optionElements = obj.get("options")
        require(optionElements != null && optionElements.isJsonArray && optionElements.asJsonArray.size() == 4) {
            "Question $id must have four options"
        }
        val options = optionElements.asJsonArray.map { option ->
            require(option.isJsonObject) { "Question $id has an invalid option" }
            AnswerOption(option.asJsonObject.requiredString("id"), option.asJsonObject.requiredString("textSv"))
        }
        require(options.map { it.id }.toSet() == setOf("A", "B", "C", "D")) { "Question $id must have option IDs A, B, C and D" }
        require(options.map { normalized(it.textSv) }.distinct().size == 4) { "Question $id has duplicate options" }
        val correct = obj.requiredString("correctOptionId")
        require(options.any { it.id == correct }) { "Question $id has an invalid correct option" }
        return Question(id, text, options, correct, category, difficulty)
    }

    private fun JsonObject.requiredString(name: String): String {
        val value = get(name)
        require(value != null && value.isJsonPrimitive && value.asJsonPrimitive.isString && value.asString.isNotBlank()) {
            "Missing or invalid field: $name"
        }
        return value.asString.trim()
    }

    private fun normalized(value: String) = value.trim().replace(Regex("\\s+"), " ").lowercase(Locale.ROOT)
}
