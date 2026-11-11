package se.yourcompany.miljonaren.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.yourcompany.miljonaren.core.ui.TvScreenScaffold
import se.yourcompany.miljonaren.core.ui.TvSecondaryButton
import se.yourcompany.miljonaren.core.ui.TvTheme

data class SettingsUiState(
    val title: String,
    val soundLabel: String,
    val soundOnLabel: String,
    val soundOffLabel: String,
    val soundEnabled: Boolean,
    val historyLabel: String,
    val historyLimit: Int,
    val clearLabel: String,
    val backLabel: String,
    val isBusy: Boolean,
    val error: String?
)

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onSoundChanged: (Boolean) -> Unit,
    onHistoryLimitChanged: (Int) -> Unit,
    onClearHistory: () -> Unit,
    onBack: () -> Unit
) {
    TvScreenScaffold {
        Column(
            modifier = Modifier.fillMaxWidth().widthIn(max = 900.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(state.title, color = TvTheme.TextPrimary, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            Text(state.soundLabel, color = TvTheme.TextPrimary, fontSize = 22.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TvSecondaryButton(state.soundOnLabel, { onSoundChanged(true) }, selected = state.soundEnabled, modifier = Modifier.weight(1f))
                TvSecondaryButton(state.soundOffLabel, { onSoundChanged(false) }, selected = !state.soundEnabled, modifier = Modifier.weight(1f))
            }
            Text(state.historyLabel, color = TvTheme.TextPrimary, fontSize = 22.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(10, 25, 50, 100, 250).forEach { limit ->
                    TvSecondaryButton(
                        label = limit.toString(), onClick = { onHistoryLimitChanged(limit) },
                        selected = state.historyLimit == limit, enabled = !state.isBusy,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            state.error?.let { Text(it, color = TvTheme.Error, fontSize = 18.sp) }
            TvSecondaryButton(state.clearLabel, onClearHistory, enabled = !state.isBusy, modifier = Modifier.fillMaxWidth())
            TvSecondaryButton(state.backLabel, onBack, modifier = Modifier.fillMaxWidth())
        }
    }
}
