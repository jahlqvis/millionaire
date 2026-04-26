package se.yourcompany.miljonaren.feature.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.yourcompany.miljonaren.core.ui.TvPrimaryButton
import se.yourcompany.miljonaren.core.ui.TvScreenScaffold
import se.yourcompany.miljonaren.core.ui.TvTheme

data class ResultsPlayerUiState(
    val placement: Int,
    val name: String,
    val score: Int,
    val rowLabel: String
)

data class ResultsUiState(
    val title: String,
    val isTie: Boolean,
    val tieLabel: String,
    val winnerLabel: String,
    val restartLabel: String,
    val players: List<ResultsPlayerUiState>
)

@Composable
fun ResultsScreen(
    state: ResultsUiState,
    onRestart: () -> Unit
) {
    TvScreenScaffold {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 900.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = state.title,
                color = TvTheme.TextPrimary,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )

            Surface(
                color = TvTheme.SurfaceAlt,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (state.isTie) state.tieLabel else state.winnerLabel,
                    color = if (state.isTie) TvTheme.TextPrimary else TvTheme.Accent,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
                )
            }

            state.players.forEach { player ->
                Surface(
                    color = TvTheme.Surface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = player.rowLabel,
                        color = TvTheme.TextPrimary,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }

            TvPrimaryButton(
                label = state.restartLabel,
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
