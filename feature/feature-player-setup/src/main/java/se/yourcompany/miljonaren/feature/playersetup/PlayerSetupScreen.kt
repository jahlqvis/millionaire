package se.yourcompany.miljonaren.feature.playersetup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.yourcompany.miljonaren.core.ui.TvPrimaryButton
import se.yourcompany.miljonaren.core.ui.TvScreenScaffold
import se.yourcompany.miljonaren.core.ui.TvSecondaryButton
import se.yourcompany.miljonaren.core.ui.TvTheme

data class PlayerSetupUiState(
    val title: String,
    val playerNameLabels: List<String>,
    val backButtonLabel: String,
    val startButtonLabel: String,
    val playerCount: Int,
    val names: List<String>,
    val validationError: String?
)

@Composable
fun PlayerSetupScreen(
    state: PlayerSetupUiState,
    onPlayerCountChanged: (Int) -> Unit,
    onPlayerNameChanged: (Int, String) -> Unit,
    onStartGame: () -> Unit,
    onBack: () -> Unit
) {
    TvScreenScaffold {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 900.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = state.title,
                color = TvTheme.TextPrimary,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                (1..4).forEach { count ->
                    TvSecondaryButton(
                        label = count.toString(),
                        onClick = { onPlayerCountChanged(count) },
                        selected = state.playerCount == count,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            repeat(state.playerCount) { index ->
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.names[index],
                    onValueChange = { onPlayerNameChanged(index, it) },
                    label = {
                        Text(
                            text = state.playerNameLabels[index],
                            color = TvTheme.TextSecondary
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = TvTheme.TextPrimary,
                        unfocusedTextColor = TvTheme.TextPrimary,
                        focusedContainerColor = TvTheme.Surface,
                        unfocusedContainerColor = TvTheme.Surface,
                        focusedIndicatorColor = TvTheme.Accent,
                        unfocusedIndicatorColor = TvTheme.SurfaceAlt,
                        cursorColor = TvTheme.Accent
                    )
                )
            }

            state.validationError?.let { message ->
                Text(
                    text = message,
                    color = TvTheme.Error,
                    fontSize = 18.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TvSecondaryButton(
                    label = state.backButtonLabel,
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                )
                TvPrimaryButton(
                    label = state.startButtonLabel,
                    onClick = onStartGame,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
