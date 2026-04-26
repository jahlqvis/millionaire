package se.yourcompany.miljonaren.feature.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = state.title)
        if (state.isTie) {
            Text(text = state.tieLabel)
        } else {
            Text(text = state.winnerLabel)
        }

        state.players.forEach { player ->
            Text(text = player.rowLabel)
        }

        Button(onClick = onRestart) {
            Text(text = state.restartLabel)
        }
    }
}
