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
    val score: Int
)

data class ResultsUiState(
    val isTie: Boolean,
    val winnerName: String?,
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
        Text(text = "Resultat")
        if (state.isTie) {
            Text(text = "Oavgjort")
        } else {
            Text(text = "Vinnare: ${state.winnerName ?: "-"}")
        }

        state.players.forEach { player ->
            Text(text = "${player.placement}. ${player.name} - ${player.score} poang")
        }

        Button(onClick = onRestart) {
            Text(text = "Till startsidan")
        }
    }
}
