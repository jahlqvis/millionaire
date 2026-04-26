package se.yourcompany.miljonaren.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

data class HomeUiState(
    val title: String,
    val primaryActionLabel: String
)

@Composable
fun HomeScreen(
    state: HomeUiState,
    onStartGame: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = state.title)
        Button(onClick = onStartGame) {
            Text(state.primaryActionLabel)
        }
    }
}
