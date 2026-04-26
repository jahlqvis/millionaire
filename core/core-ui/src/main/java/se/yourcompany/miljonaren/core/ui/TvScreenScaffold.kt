package se.yourcompany.miljonaren.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TvScreenScaffold(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TvTheme.Background)
            .padding(horizontal = 56.dp, vertical = 36.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.widthIn(max = 1280.dp),
            content = content
        )
    }
}
