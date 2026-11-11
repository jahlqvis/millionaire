package se.yourcompany.miljonaren.data.local.content

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.util.Locale
import se.yourcompany.miljonaren.domain.model.Question
import se.yourcompany.miljonaren.domain.model.QuestionTheme

data class BundledQuestionPack(val theme: QuestionTheme, val questions: List<Question>)

/** Loads the explicitly indexed packs; directory listing is not reliable inside an Android APK. */
object BundledQuestionPacks {
    private const val MANIFEST = "/question-packs.json"
    private val themeIdPattern = Regex("[a-z0-9][a-z0-9_-]{0,63}")
    private val resourcePattern = Regex("/(questions\\.json|question-packs/[a-zA-Z0-9_-]+\\.json)")

    fun load(readResource: (String) -> String? = ::readBundledResource): List<BundledQuestionPack> {
        val manifestText = requireNotNull(readResource(MANIFEST)) { "Question pack manifest is missing" }
        val manifest = JsonParser.parseString(manifestText)
        require(manifest.isJsonObject) { "Question pack manifest must be an object" }
        val root = manifest.asJsonObject
        val version = root.get("version")
        require(version != null && version.isJsonPrimitive && version.asJsonPrimitive.isNumber && version.asString == "1") {
            "Unsupported question pack manifest version"
        }
        val items = root.get("packs")
        require(items != null && items.isJsonArray && items.asJsonArray.size() > 0) {
            "Question pack manifest must contain at least one pack"
        }
        val packs = items.asJsonArray.map { entry ->
            require(entry.isJsonObject) { "Question pack entry must be an object" }
            val obj = entry.asJsonObject
            val id = obj.requiredString("id")
            val name = obj.requiredString("nameSv")
            val resource = obj.requiredString("resource")
            require(themeIdPattern.matches(id)) { "Invalid question pack ID: $id" }
            require(resourcePattern.matches(resource)) { "Invalid question pack resource: $resource" }
            val json = requireNotNull(readResource(resource)) { "Question pack resource not found: $resource" }
            BundledQuestionPack(QuestionTheme(id, name), QuestionPackParser.parse(json))
        }
        require(packs.map { it.theme.id }.distinct().size == packs.size) { "Duplicate question pack ID" }
        require(packs.map { it.theme.nameSv.lowercase(Locale.ROOT) }.distinct().size == packs.size) {
            "Duplicate question pack name"
        }
        return packs
    }

    private fun readBundledResource(path: String): String? =
        BundledQuestionPacks::class.java.getResourceAsStream(path)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }

    private fun JsonObject.requiredString(name: String): String {
        val value = get(name)
        require(value != null && value.isJsonPrimitive && value.asJsonPrimitive.isString && value.asString.isNotBlank()) {
            "Missing or invalid manifest field: $name"
        }
        return value.asString.trim()
    }
}
