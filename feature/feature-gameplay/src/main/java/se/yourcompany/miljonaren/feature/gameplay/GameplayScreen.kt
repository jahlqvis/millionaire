package se.yourcompany.miljonaren.feature.gameplay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.yourcompany.miljonaren.core.ui.TvOptionCard
import se.yourcompany.miljonaren.core.ui.TvScreenScaffold
import se.yourcompany.miljonaren.core.ui.TvSecondaryButton
import se.yourcompany.miljonaren.core.ui.TvTheme

data class GameplayOptionUiState(
    val id: String,
    val displayText: String,
    val isEnabled: Boolean
)

data class AudienceVoteUiState(val label: String, val percentage: Int)

data class LifelineAdviceUiState(
    val title: String,
    val message: String?,
    val votes: List<AudienceVoteUiState>,
    val continueLabel: String
)

data class GameplayUiState(
    val roundLabel: String,
    val turnLabel: String,
    val timerLabel: String?,
    val fiftyFiftyLabel: String,
    val isFiftyFiftyAvailable: Boolean,
    val audienceLabel: String,
    val isAudienceAvailable: Boolean,
    val friendLabel: String,
    val isFriendAvailable: Boolean,
    val advice: LifelineAdviceUiState?,
    val questionLabel: String,
    val options: List<GameplayOptionUiState>,
    val answerFeedback: String?,
    val correctAnswerLabel: String?,
    val isAnswerCorrect: Boolean?,
    val answerLocked: Boolean
)

@Composable
fun GameplayScreen(
    state: GameplayUiState,
    onUseFiftyFifty: () -> Unit,
    onUseAudience: () -> Unit,
    onUseFriend: () -> Unit,
    onDismissAdvice: () -> Unit,
    onPause: () -> Unit,
    onAnswerSelected: (String) -> Unit
) {
    state.advice?.let { advice ->
        AlertDialog(
            onDismissRequest = onPause,
            title = { Text(advice.title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    advice.message?.let { Text(it) }
                    advice.votes.forEach { vote ->
                        Text("${vote.label}: ${vote.percentage}%")
                        LinearProgressIndicator(
                            progress = vote.percentage / 100f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = onDismissAdvice) { Text(advice.continueLabel) } }
        )
    }
    TvScreenScaffold {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 1050.dp)
                .verticalScroll(rememberScrollState()),
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
            state.timerLabel?.let { label ->
                Text(text = label, color = TvTheme.TextPrimary, fontSize = 22.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TvSecondaryButton(
                    label = state.fiftyFiftyLabel,
                    onClick = onUseFiftyFifty,
                    enabled = state.isFiftyFiftyAvailable,
                    selected = !state.isFiftyFiftyAvailable,
                    modifier = Modifier.weight(1f)
                )
                TvSecondaryButton(
                    label = state.audienceLabel,
                    onClick = onUseAudience,
                    enabled = state.isAudienceAvailable,
                    selected = !state.isAudienceAvailable,
                    modifier = Modifier.weight(1f)
                )
                TvSecondaryButton(
                    label = state.friendLabel,
                    onClick = onUseFriend,
                    enabled = state.isFriendAvailable,
                    selected = !state.isFriendAvailable,
                    modifier = Modifier.weight(1f)
                )
            }

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
                    enabled = !state.answerLocked && option.isEnabled
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
                state.correctAnswerLabel?.let { label ->
                    Text(text = label, color = TvTheme.TextPrimary, fontSize = 22.sp)
                }
            }
        }
    }
}
