package se.yourcompany.miljonaren.data.repository

import android.content.Context
import org.json.JSONArray

/** Separate from game history so clearing history does not clear question-use tracking. */
class SharedPreferencesQuestionUsageStore(context: Context) : QuestionUsageStore {
    private val preferences = context.applicationContext.getSharedPreferences("question_usage", Context.MODE_PRIVATE)

    override fun load(): List<List<String>> = runCatching {
        val games = JSONArray(preferences.getString("completed_games", "[]"))
        (0 until games.length()).map { index ->
            val questions = games.getJSONArray(index)
            (0 until questions.length()).map { questions.getString(it) }
        }.takeLast(10)
    }.getOrDefault(emptyList())

    override fun save(games: List<List<String>>) {
        val encoded = JSONArray()
        games.takeLast(10).forEach { game -> encoded.put(JSONArray(game)) }
        preferences.edit().putString("completed_games", encoded.toString()).apply()
    }
}
