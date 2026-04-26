package se.yourcompany.miljonaren.feature.playersetup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = state.title)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..4).forEach { count ->
                Button(onClick = { onPlayerCountChanged(count) }) {
                    Text(text = count.toString())
                }
            }
        }

        repeat(state.playerCount) { index ->
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.names[index],
                onValueChange = { onPlayerNameChanged(index, it) },
                label = { Text(state.playerNameLabels[index]) },
                singleLine = true
            )
        }

        state.validationError?.let { message ->
            Text(text = message)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBack) {
                Text(text = state.backButtonLabel)
            }
            Button(onClick = onStartGame) {
                Text(text = state.startButtonLabel)
            }
        }
    }
}
