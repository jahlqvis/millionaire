package se.yourcompany.miljonaren.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.yourcompany.miljonaren.core.ui.TvPrimaryButton
import se.yourcompany.miljonaren.core.ui.TvScreenScaffold
import se.yourcompany.miljonaren.core.ui.TvTheme

data class HomeUiState(
    val title: String,
    val subtitle: String,
    val primaryActionLabel: String
)

@Composable
fun HomeScreen(
    state: HomeUiState,
    onStartGame: () -> Unit
) {
    TvScreenScaffold {
        Column(
            modifier = Modifier.widthIn(max = 720.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = state.title,
                color = TvTheme.TextPrimary,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = state.subtitle,
                color = TvTheme.TextSecondary,
                fontSize = 24.sp
            )
            TvPrimaryButton(
                label = state.primaryActionLabel,
                onClick = onStartGame,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
