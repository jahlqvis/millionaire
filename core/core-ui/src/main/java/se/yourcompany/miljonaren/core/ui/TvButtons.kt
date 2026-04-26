package se.yourcompany.miljonaren.core.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TvPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(18.dp)

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = shape,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isFocused) TvTheme.Accent else TvTheme.SurfaceAlt,
            contentColor = if (isFocused) Color(0xFF121212) else TvTheme.TextPrimary,
            disabledContainerColor = TvTheme.Surface,
            disabledContentColor = TvTheme.TextSecondary
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .graphicsLayer {
                val scale = if (isFocused) 1.03f else 1f
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) TvTheme.Accent else TvTheme.Surface,
                shape = shape
            )
    ) {
        Text(
            text = label,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TvSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(16.dp)

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = shape,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                selected -> TvTheme.SurfaceAlt
                isFocused -> TvTheme.SurfaceAlt
                else -> TvTheme.Surface
            },
            contentColor = TvTheme.TextPrimary,
            disabledContainerColor = TvTheme.Surface,
            disabledContentColor = TvTheme.TextSecondary
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .graphicsLayer {
                val scale = if (isFocused) 1.02f else 1f
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = when {
                    isFocused -> TvTheme.Accent
                    selected -> TvTheme.Accent
                    else -> TvTheme.SurfaceAlt
                },
                shape = shape
            )
    ) {
        Text(
            text = label,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
