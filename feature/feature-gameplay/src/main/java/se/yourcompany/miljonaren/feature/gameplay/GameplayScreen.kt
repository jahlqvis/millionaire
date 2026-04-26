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
import se.yourcompany.miljonaren.domain.model.Player
import se.yourcompany.miljonaren.domain.model.Question

@Composable
fun GameplayScreen(
    activePlayer: Player,
    currentRound: Int,
    maxRounds: Int,
    question: Question,
    answerFeedback: String?,
    answerLocked: Boolean,
    onAnswerSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Runda $currentRound av $maxRounds")
        Text(text = "Tur: ${activePlayer.name}")
        Text(text = "Fraga: ${question.textSv}")

        question.options.forEach { option ->
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !answerLocked,
                onClick = { onAnswerSelected(option.id) }
            ) {
                Text(text = "${option.id}: ${option.textSv}")
            }
        }

        if (answerFeedback != null) {
            Text(text = answerFeedback)
        }
    }
}
