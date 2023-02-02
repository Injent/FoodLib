package me.injent.foodlib.ui.components

import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.injent.foodlib.ui.theme.FoodLibTheme

@Composable
fun FoodLibDivider(
    modifier: Modifier = Modifier,
    color: Color = FoodLibTheme.colorScheme.border,
    thickness: Dp = 1.dp
) {
    Divider(
        modifier = modifier,
        color = color,
        thickness = thickness
    )
}