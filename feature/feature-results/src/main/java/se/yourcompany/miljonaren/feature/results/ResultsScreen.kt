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
import se.yourcompany.miljonaren.domain.model.GameResult

@Composable
fun ResultsScreen(
    result: GameResult,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Resultat")
        if (result.isTie) {
            Text(text = "Oavgjort")
        } else {
            Text(text = "Vinnare: ${result.winner?.name ?: "-"}")
        }

        result.rankedPlayers.forEachIndexed { index, player ->
            Text(text = "${index + 1}. ${player.name} - ${player.score} poang")
        }

        Button(onClick = onRestart) {
            Text(text = "Till startsidan")
        }
    }
}
