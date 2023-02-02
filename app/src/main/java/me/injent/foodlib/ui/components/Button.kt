package me.injent.foodlib.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.injent.foodlib.ui.theme.FoodLibTheme

@Composable
fun SmallTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    textColor: Color = FoodLibTheme.colorScheme.primary,
    text: String,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
) {
    ClickableText(
        onClick = onClick,
        modifier = modifier,
        rippleColor = textColor.copy(.1f)
    ) {
        Text(
            modifier = Modifier
                .padding(contentPadding)
                .align(Alignment.Center),
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun FoodLibButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    shape: Shape = RoundedCornerShape(12.dp),
    color: Color = FoodLibTheme.colorScheme.container,
    contentColor: Color = FoodLibTheme.colorScheme.primary,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
    text: String
) {
    Button(
        modifier = modifier.padding(contentPadding),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = contentColor
        ),
        shape = shape
    ) {
        Text(text = text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}