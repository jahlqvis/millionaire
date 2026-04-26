package se.yourcompany.miljonaren.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TvOptionCard(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .graphicsLayer {
                val scale = if (isFocused && enabled) 1.02f else 1f
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(
                if (!enabled) TvTheme.Surface
                else if (isFocused) TvTheme.SurfaceAlt
                else TvTheme.Surface
            )
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) TvTheme.Accent else TvTheme.SurfaceAlt,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .focusable(enabled = enabled, interactionSource = interactionSource)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = label,
            color = if (enabled) TvTheme.TextPrimary else TvTheme.TextSecondary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
