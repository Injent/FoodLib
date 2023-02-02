package me.injent.foodlib.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.injent.foodlib.ui.theme.FoodLibTheme

@Composable
fun HeaderText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = FoodLibTheme.colorScheme.textPrimary
) {
    Text(
        text = text,
        color = color,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
    )
}

@Composable
fun ClickableText(
    modifier: Modifier = Modifier,
    rippleColor: Color = Color.Unspecified,
    shape: Shape = RoundedCornerShape(6.dp),
    onClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
    text: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = rippleColor)
            )
            .padding(contentPadding)
    ) {
        text()
    }
}