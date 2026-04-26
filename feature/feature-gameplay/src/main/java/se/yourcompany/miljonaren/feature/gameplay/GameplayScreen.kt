package se.yourcompany.miljonaren.feature.gameplay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.yourcompany.miljonaren.core.ui.TvOptionCard
import se.yourcompany.miljonaren.core.ui.TvScreenScaffold
import se.yourcompany.miljonaren.core.ui.TvTheme

data class GameplayOptionUiState(
    val id: String,
    val displayText: String
)

data class GameplayUiState(
    val roundLabel: String,
    val turnLabel: String,
    val questionLabel: String,
    val options: List<GameplayOptionUiState>,
    val answerFeedback: String?,
    val isAnswerCorrect: Boolean?,
    val answerLocked: Boolean
)

@Composable
fun GameplayScreen(
    state: GameplayUiState,
    onAnswerSelected: (String) -> Unit
) {
    TvScreenScaffold {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 1050.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = state.roundLabel,
                color = TvTheme.TextSecondary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = state.turnLabel,
                color = TvTheme.Accent,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold
            )

            Surface(
                color = TvTheme.Surface,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = state.questionLabel,
                    color = TvTheme.TextPrimary,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )
            }

            state.options.forEach { option ->
                TvOptionCard(
                    label = option.displayText,
                    onClick = { onAnswerSelected(option.id) },
                    enabled = !state.answerLocked
                )
            }

            if (state.answerFeedback != null) {
                Text(
                    text = state.answerFeedback,
                    color = if (state.isAnswerCorrect == true) {
                        TvTheme.Success
                    } else {
                        TvTheme.Error
                    },
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
