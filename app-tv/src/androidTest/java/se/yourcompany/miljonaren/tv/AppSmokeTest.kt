package se.yourcompany.miljonaren.tv

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class AppSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun happyPath_startUseFiftyFiftyFinishAndSeeHistoryEntry() {
        composeRule.onNodeWithText("Starta spel").performClick()
        composeRule.onNodeWithText("1").performClick()
        composeRule.onNodeWithText("Starta spel").performClick()

        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("50/50").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("50/50").performClick()

        repeat(5) {
            composeRule.waitUntil(timeoutMillis = 10_000) {
                composeRule.onAllNodesWithText("A:", substring = true).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithText("A:", substring = true).performClick()
            Thread.sleep(1300)
        }

        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("Resultat").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Till startsidan").performClick()

        composeRule.onNodeWithText("Historik").performClick()
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("Vinnare:", substring = true).fetchSemanticsNodes()
                .isNotEmpty() ||
                composeRule.onAllNodesWithText("Oavgjort").fetchSemanticsNodes().isNotEmpty()
        }
    }
}
