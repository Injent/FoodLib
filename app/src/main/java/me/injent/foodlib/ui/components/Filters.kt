package me.injent.foodlib.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.injent.foodlib.domain.model.Filter
import me.injent.foodlib.domain.model.foodTypeFilters
import me.injent.foodlib.ui.theme.FoodLibTheme

@Composable
fun FilterBar(
    modifier: Modifier = Modifier,
    filters: List<Filter>,
    onShowFilters: () -> Unit
) {
    LazyRow(
        modifier = modifier.heightIn(min = 56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 12.dp, end = 8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                filter = filter,
                shape = CircleShape
            )
        }
    }
}

@Composable
fun FilterChip(
    modifier: Modifier = Modifier,
    filter: Filter,
    shape: Shape = MaterialTheme.shapes.small
) {
    val (selected, setSelected) = filter.enabled
    val background by animateColorAsState(
        if (selected) FoodLibTheme.colorScheme.primary else FoodLibTheme.colorScheme.secondary
    )
    val textColor by animateColorAsState(
        if (selected) FoodLibTheme.colorScheme.surface else FoodLibTheme.colorScheme.primary
    )

    FoodLibSurface(
        modifier = modifier
            .height(44.dp),
        shape = shape,
        color = background,
        contentColor = textColor
    ) {
        val interactionSource = remember { MutableInteractionSource() }

        val pressed by interactionSource.collectIsPressedAsState()
        val backgroundPressed =
            if (pressed)
                FoodLibTheme.colorScheme.primary.copy(.25f)
            else
                Color.Transparent

        Box(
            modifier = Modifier
                .toggleable(
                    value = selected,
                    onValueChange = setSelected,
                    interactionSource = interactionSource,
                    indication = null
                )
                .background(backgroundPressed)
        ) {
            Text(
                text = stringResource(id = filter.name),
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 6.dp
                )
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun FilterBarPreview() {
    FoodLibTheme {
        FilterBar(filters = foodTypeFilters, onShowFilters = {})
    }
}