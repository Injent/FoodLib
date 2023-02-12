package me.injent.foodlib.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.injent.foodlib.ui.theme.FoodLibTheme

@Composable
fun FoodLibTextButton(
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
fun FoodLibIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    color: Color = FoodLibTheme.colorScheme.primary,
    shape: Shape = MaterialTheme.shapes.medium,
    contentColor: Color = FoodLibTheme.colorScheme.surface,
    text: String? = null,
    imageVector: ImageVector
) {
    Row(
        modifier = modifier
            .clip(shape)
            .background(color = color)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = imageVector,
            tint = contentColor,
            contentDescription = null
        )
        text?.let {
            LargeBodyText(text = text, color = contentColor)
        }
    }
}

@Composable
fun FoodLibIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    color: Color = FoodLibTheme.colorScheme.primary,
    shape: Shape = MaterialTheme.shapes.medium,
    contentColor: Color = FoodLibTheme.colorScheme.surface,
    text: String? = null,
    @DrawableRes drawable: Int
) {
    Row(
        modifier = modifier
            .background(color = color, shape = shape)
            .clickable(onClick = onClick)
            .padding(12.dp)
            .wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            painter = painterResource(id = drawable),
            tint = contentColor,
            contentDescription = null
        )
        text?.let {
            LargeBodyText(text = text, color = contentColor)
        }
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