package me.injent.foodlib.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import me.injent.foodlib.R
import me.injent.foodlib.ui.theme.FoodLibTheme

@Composable
fun Categories(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    categories: ImmutableList<Category>,
    onCategorySelected: (category: Category) -> Unit
) {
    val state = rememberLazyListState()

    LazyRow(
        modifier = modifier,
        state = state,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = contentPadding
    ) {
        items(categories) { category ->
            Box(
                modifier = Modifier.clip(MaterialTheme.shapes.medium)
            ) {
                ImageCard(
                    modifier = Modifier
                        .width(150.dp)
                        .height(100.dp),
                    onClick = {
                        onCategorySelected(category)
                    },
                    painter = painterResource(id = category.drawable)
                )
                Box(
                    Modifier
                        .width(150.dp)
                        .height(100.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black
                                )
                            ),
                            MaterialTheme.shapes.medium
                        )
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 10.dp, bottom = 12.dp),
                        text = stringResource(id = category.name),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun CategoriesPreview() {
    FoodLibTheme {
        Categories(
            categories = categories,
            contentPadding = PaddingValues(0.dp),
            onCategorySelected = {}
        )
    }
}

data class Category(
    @StringRes val name: Int,
    @DrawableRes val drawable: Int
)

val categories = persistentListOf(
    Category(R.string.breakfast, R.drawable.breakfast),
    Category(R.string.lunch, R.drawable.lunch),
    Category(R.string.dinner, R.drawable.dinner),
    Category(R.string.desserts, R.drawable.dessert)
)