package se.yourcompany.miljonaren.feature.gameplay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class GameplayOptionUiState(
    val id: String,
    val text: String
)

data class GameplayUiState(
    val currentRound: Int,
    val maxRounds: Int,
    val activePlayerName: String,
    val questionText: String,
    val options: List<GameplayOptionUiState>,
    val answerFeedback: String?,
    val answerLocked: Boolean
)

@Composable
fun GameplayScreen(
    state: GameplayUiState,
    onAnswerSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Runda ${state.currentRound} av ${state.maxRounds}")
        Text(text = "Tur: ${state.activePlayerName}")
        Text(text = "Fraga: ${state.questionText}")

        state.options.forEach { option ->
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.answerLocked,
                onClick = { onAnswerSelected(option.id) }
            ) {
                Text(text = "${option.id}: ${option.text}")
            }
        }

        if (state.answerFeedback != null) {
            Text(text = state.answerFeedback)
        }
    }
}
