package me.injent.foodlib.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.injent.foodlib.ui.theme.FoodLibTheme

@Composable
fun FoodLibCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    color: Color = FoodLibTheme.colorScheme.surface,
    contentColor: Color = FoodLibTheme.colorScheme.textPrimary,
    border: BorderStroke? = null,
    elevation: Dp = 4.dp,
    content: @Composable () -> Unit
) {
    FoodLibSurface(
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
        elevation = elevation,
        border = border,
        content = content
    )
}

@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    painter: Painter,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier.clickable { onClick() }
    ) {
        Image(
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier.clip(shape)
        )
    }
}