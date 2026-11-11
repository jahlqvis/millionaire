package se.yourcompany.miljonaren.data.local.content

object SwedishQuestionSeed {
    val questions by lazy {
        val input = requireNotNull(SwedishQuestionSeed::class.java.getResourceAsStream("/questions.json")) {
            "Bundled question pack is missing"
        }
        input.bufferedReader(Charsets.UTF_8).use { QuestionPackParser.parse(it.readText()) }
    }
}
