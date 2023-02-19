package me.injent.foodlib.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import me.injent.foodlib.ui.theme.FoodLibTheme
import me.injent.foodlib.util.foodLibShadow

@Composable
fun FoodLibSnackBar(
    @DrawableRes drawableRes: Int,
    message: String,
    containerColor: Color = FoodLibTheme.colorScheme.surface
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .foodLibShadow()
            .background(
                color = containerColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = drawableRes),
            tint = FoodLibTheme.colorScheme.primary,
            contentDescription = null
        )
        SmallHeadLineText(text = message)
    }
}