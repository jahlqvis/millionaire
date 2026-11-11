package se.yourcompany.miljonaren.tv

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import se.yourcompany.miljonaren.feature.playersetup.PlayerSetupScreen
import se.yourcompany.miljonaren.feature.playersetup.PlayerSetupUiState
import se.yourcompany.miljonaren.feature.playersetup.QuestionThemeUiState

class QuestionThemeSelectionTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun setup_canSelectOneOfSeveralThemes() {
        var chosen = "general"
        composeRule.setContent {
            var selected by remember { mutableStateOf("general") }
            PlayerSetupScreen(
                state = PlayerSetupUiState(
                    title = "Spelare", playerNameLabels = listOf("Spelare 1"),
                    backButtonLabel = "Tillbaka", startButtonLabel = "Starta spel",
                    themeLabel = "Frågetema", themes = listOf(
                        QuestionThemeUiState("general", "Blandat"),
                        QuestionThemeUiState("space", "Rymden")
                    ), selectedThemeId = selected,
                    roundsLabel = "Antal rundor", roundCount = 5,
                    timerLabel = "Tidsgräns", timerOffLabel = "Av", timerOnLabel = "På",
                    timerEnabled = false, playerCount = 1, names = listOf("Anna"), validationError = null
                ),
                onPlayerCountChanged = {}, onPlayerNameChanged = { _, _ -> },
                onThemeChanged = { chosen = it; selected = it },
                onRoundCountChanged = {}, onTimerEnabledChanged = {}, onStartGame = {}, onBack = {}
            )
        }
        composeRule.onNodeWithText("Rymden").performScrollTo().performClick()
        composeRule.runOnIdle { assertEquals("space", chosen) }
    }
}
