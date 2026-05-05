package se.yourcompany.miljonaren.feature.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import se.yourcompany.miljonaren.core.ui.TvSecondaryButton
import se.yourcompany.miljonaren.core.ui.TvTheme

data class HistoryGameItemUiState(
    val sessionId: String,
    val title: String,
    val subtitle: String,
    val isSelected: Boolean
)

data class HistoryPlayerItemUiState(
    val rowLabel: String
)

data class HistoryUiState(
    val title: String,
    val backLabel: String,
    val loadingLabel: String,
    val emptyLabel: String,
    val isLoading: Boolean,
    val games: List<HistoryGameItemUiState>,
    val selectedResults: List<HistoryPlayerItemUiState>
)

@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onBack: () -> Unit,
    onSelectGame: (String) -> Unit
) {
    TvScreenScaffold {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 1080.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = state.title,
                    color = TvTheme.TextPrimary,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TvSecondaryButton(
                    label = state.backLabel,
                    onClick = onBack,
                    modifier = Modifier.widthIn(min = 180.dp, max = 220.dp)
                )
            }

            if (state.isLoading) {
                Text(
                    text = state.loadingLabel,
                    color = TvTheme.TextSecondary,
                    fontSize = 22.sp
                )
            } else if (state.games.isEmpty()) {
                Surface(
                    color = TvTheme.Surface,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.emptyLabel,
                        color = TvTheme.TextSecondary,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            } else {
                state.games.forEach { game ->
                    TvOptionCard(
                        label = game.title,
                        onClick = { onSelectGame(game.sessionId) },
                        enabled = true,
                        modifier = if (game.isSelected) {
                            Modifier.fillMaxWidth()
                        } else {
                            Modifier.fillMaxWidth()
                        }
                    )
                    Text(
                        text = game.subtitle,
                        color = TvTheme.TextSecondary,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )

                    if (game.isSelected && state.selectedResults.isNotEmpty()) {
                        Surface(
                            color = TvTheme.Surface,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                state.selectedResults.forEach { player ->
                                    Text(
                                        text = player.rowLabel,
                                        color = TvTheme.TextPrimary,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
