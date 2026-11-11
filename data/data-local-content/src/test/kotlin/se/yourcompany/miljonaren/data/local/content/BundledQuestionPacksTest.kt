package se.yourcompany.miljonaren.data.local.content

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class BundledQuestionPacksTest {
    private val questions = """{"version":1,"questions":[{"id":"q1","textSv":"Fråga?","category":"Test","difficulty":"EASY","options":[{"id":"A","textSv":"Ett"},{"id":"B","textSv":"Två"},{"id":"C","textSv":"Tre"},{"id":"D","textSv":"Fyra"}],"correctOptionId":"A"}]}"""

    private fun load(manifest: String, resources: Map<String, String> = mapOf("/questions.json" to questions)) =
        BundledQuestionPacks.load { path -> if (path == "/question-packs.json") manifest else resources[path] }

    @Test
    fun bundledManifestAndEveryPack_areValid() {
        val packs = BundledQuestionPacks.load()
        assertTrue(packs.isNotEmpty())
        assertEquals("general", packs.first().theme.id)
        assertTrue(packs.all { it.questions.isNotEmpty() })
    }

    @Test
    fun load_multipleNamedPacks() {
        val manifest = """{"version":1,"packs":[{"id":"general","nameSv":"Blandat","resource":"/questions.json"},{"id":"space","nameSv":"Rymden","resource":"/question-packs/space.json"}]}"""
        val packs = load(manifest, mapOf("/questions.json" to questions, "/question-packs/space.json" to questions))
        assertEquals(listOf("general", "space"), packs.map { it.theme.id })
        assertEquals(listOf("Blandat", "Rymden"), packs.map { it.theme.nameSv })
    }

    @Test
    fun load_rejectsInvalidManifestAndMissingPacks() {
        val badManifests = listOf(
            """{"version":2,"packs":[]}""",
            """{"version":1,"packs":[]}""",
            """{"version":1,"packs":[{"id":"bad/id","nameSv":"Fel","resource":"/questions.json"}]}""",
            """{"version":1,"packs":[{"id":"bad","nameSv":"Fel","resource":"/../questions.json"}]}""",
            """{"version":1,"packs":[{"id":"missing","nameSv":"Saknas","resource":"/question-packs/missing.json"}]}""",
            """{"version":1,"packs":[{"id":"same","nameSv":"En","resource":"/questions.json"},{"id":"same","nameSv":"Två","resource":"/questions.json"}]}""",
            """{"version":1,"packs":[{"id":"one","nameSv":"Blandat","resource":"/questions.json"},{"id":"two","nameSv":"blandat","resource":"/questions.json"}]}"""
        )
        badManifests.forEach { manifest ->
            assertThrows(IllegalArgumentException::class.java) { load(manifest) }
        }
    }
}
