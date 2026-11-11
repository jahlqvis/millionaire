package se.yourcompany.miljonaren.tv

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.onFirst
import android.view.KeyEvent
import org.junit.Rule
import org.junit.Test

class AppSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun remoteCanOpenSetupAndStartGame() {
        fun press(key: Int) {
            composeRule.runOnIdle {
                composeRule.activity.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, key))
                composeRule.activity.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, key))
            }
        }
        press(KeyEvent.KEYCODE_DPAD_DOWN)
        press(KeyEvent.KEYCODE_DPAD_CENTER)
        composeRule.onNodeWithText("Välj antal spelare").assertExists()
        repeat(10) { press(KeyEvent.KEYCODE_DPAD_DOWN) }
        press(KeyEvent.KEYCODE_DPAD_RIGHT)
        press(KeyEvent.KEYCODE_DPAD_CENTER)
        composeRule.onNodeWithText("50/50").assertExists()
    }

    @Test
    fun backPausesAndResumeReturnsToSameQuestion() {
        composeRule.onNodeWithText("Starta spel").performClick()
        composeRule.onNodeWithText("1").performClick()
        composeRule.onNodeWithText("Starta spel").performScrollTo().performClick()
        composeRule.onNodeWithText("50/50").assertExists()
        composeRule.runOnIdle { composeRule.activity.onBackPressedDispatcher.onBackPressed() }
        composeRule.onNodeWithText("Fortsätt spel").assertExists()
        composeRule.onNodeWithText("Starta spel").performClick()
        composeRule.onNodeWithText("Avbryt").performClick()
        composeRule.onNodeWithText("Fortsätt spel").performClick()
        composeRule.onNodeWithText("50/50").assertExists()
    }

    @Test
    fun lifelinesCanBeCombinedAndAdviceSurvivesPause() {
        composeRule.onNodeWithText("Starta spel").performClick()
        composeRule.onNodeWithText("1").performClick()
        composeRule.onNodeWithText("30 sekunder").performScrollTo().performClick()
        composeRule.onNodeWithText("Starta spel").performScrollTo().performClick()
        composeRule.onNodeWithText("50/50").performScrollTo().performClick()
        composeRule.onNodeWithText("Fråga publiken").performScrollTo().performClick()
        composeRule.onNodeWithText("Fortsätt").assertExists()
        org.junit.Assert.assertEquals(4, composeRule.onAllNodesWithText("%", substring = true).fetchSemanticsNodes().size)
        composeRule.onNodeWithText("Fortsätt").performClick()
        composeRule.onNodeWithText("Ring en vän").performScrollTo().performClick()
        composeRule.onNodeWithText("Jag tror att svaret", substring = true).assertExists()
        composeRule.runOnIdle { composeRule.activity.onBackPressedDispatcher.onBackPressed() }
        composeRule.onNodeWithText("Fortsätt spel").performClick()
        composeRule.onNodeWithText("Jag tror att svaret", substring = true).assertExists()
        composeRule.onNodeWithText("Fortsätt").performClick()
        composeRule.onNodeWithText("Tid kvar:", substring = true).assertExists()
    }

    @Test
    fun settingsAreAccessibleFromHome() {
        composeRule.onNodeWithText("Inställningar").performClick()
        composeRule.onNodeWithText("Ljudeffekter").assertExists()
        composeRule.onNodeWithText("Antal sparade spel").assertExists()
        composeRule.onNodeWithText("Rensa historik").assertExists()
        composeRule.onNodeWithText("Tillbaka").performScrollTo().performClick()
        composeRule.onNodeWithText("Starta spel").assertExists()
    }

    @Test
    fun happyPath_startUseFiftyFiftyFinishAndSeeHistoryEntry() {
        composeRule.onNodeWithText("Starta spel").performClick()
        composeRule.onNodeWithText("1").performClick()
        composeRule.onNodeWithText("Starta spel").performScrollTo().performClick()

        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("50/50").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("50/50").performClick()

        val selectableAnswer = (hasText("A:", substring = true) or hasText("B:", substring = true) or
            hasText("C:", substring = true) or hasText("D:", substring = true)) and isEnabled()
        repeat(5) {
            composeRule.waitUntil(timeoutMillis = 10_000) {
                composeRule.onAllNodes(selectableAnswer).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onAllNodes(selectableAnswer).onFirst().performScrollTo().performClick()
        }

        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("Resultat").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Till startsidan").performClick()

        composeRule.onNodeWithText("Historik").performClick()
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("Vinnare:", substring = true).fetchSemanticsNodes()
                .isNotEmpty() ||
                composeRule.onAllNodesWithText("Delad seger", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
