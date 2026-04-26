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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

@Composable
fun PlayerSetupScreen(
    onStartGame: (List<String>) -> Unit,
    onBack: () -> Unit
) {
    var playerCount by remember { mutableIntStateOf(2) }
    val names = remember { mutableStateListOf("Spelare 1", "Spelare 2", "Spelare 3", "Spelare 4") }
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Välj antal spelare")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..4).forEach { count ->
                Button(onClick = { playerCount = count }) {
                    Text(text = count.toString())
                }
            }
        }

        repeat(playerCount) { index ->
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = names[index],
                onValueChange = { names[index] = it },
                label = { Text("Spelare ${index + 1}") },
                singleLine = true
            )
        }

        validationError?.let { message ->
            Text(text = message)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBack) {
                Text(text = "Tillbaka")
            }
            Button(
                onClick = {
                    val selectedNames = names.take(playerCount).mapIndexed { index, name ->
                        name.trim().ifBlank { "Spelare ${index + 1}" }
                    }
                    if (selectedNames.isEmpty()) {
                        validationError = "Minst en spelare kravs"
                    } else {
                        validationError = null
                        onStartGame(selectedNames)
                    }
                }
            ) {
                Text(text = "Starta omgang")
            }
        }
    }
}
