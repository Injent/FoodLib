package me.injent.foodlib.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.injent.foodlib.R
import me.injent.foodlib.data.local.datastore.Draft
import me.injent.foodlib.domain.model.Category
import me.injent.foodlib.domain.model.Recipe
import me.injent.foodlib.ui.screens.home.DraftsUiState
import me.injent.foodlib.ui.screens.home.RecipesByCategoryState
import me.injent.foodlib.ui.theme.FoodLibTheme
import me.injent.foodlib.util.RECIPES_IN_THE_ROW
import me.injent.foodlib.util.foodLibShadow
import me.injent.foodlib.util.format
import me.injent.foodlib.util.ignoreHorizontalParentPadding

fun LazyListScope.drafts(
    state: DraftsUiState,
    onDraftClick: (Draft) -> Unit
) {
    when (state) {
        DraftsUiState.NotShown -> Unit
        is DraftsUiState.Shown -> {
            item {
                MediumHeadLineText(text = stringResource(id = R.string.drafts))
            }
            item {
                Column {
                    val drafts = remember(state) { state.drafts }

                    LazyRow(
                        modifier = Modifier.ignoreHorizontalParentPadding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(drafts) { draft ->
                            val date = remember { draft.editDate.format("dd.MM / HH:mm") }

                            Draft(
                                name = draft.name,
                                content = draft.content,
                                editDate = date,
                                onClick = { onDraftClick(draft) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Draft(
    onClick: () -> Unit,
    name: String,
    editDate: String,
    content: String
) {
    Column(
        modifier = Modifier
            .width(176.dp)
            .foodLibShadow()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .background(FoodLibTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LargeBodyText(
            text = name,
            color = FoodLibTheme.colorScheme.secondary
        )
        MediumBodyText(
            modifier = Modifier.heightIn(min = 64.dp, max = 64.dp),
            text = content.ifEmpty { stringResource(id = R.string.empty_draft) },
            color = FoodLibTheme.colorScheme.textSecondary,
            maxLines = 3
        )
        SmallBodyText(
            modifier = Modifier.fillMaxWidth(),
            text = editDate,
            color = FoodLibTheme.colorScheme.textSecondary,
            textAlign = TextAlign.End
        )
    }
}

fun LazyListScope.filteredRecipes(
    state: RecipesByCategoryState,
    onRecipeClicked: (Recipe) -> Unit
) {
    item {
        MediumHeadLineText(text = stringResource(id = R.string.recipes))
    }
    when (state) {
        RecipesByCategoryState.Loading -> {
            item {
                Box(modifier= Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        color = FoodLibTheme.colorScheme.primary
                    )
                }
            }
        }
        is RecipesByCategoryState.Shown -> {
            items(state.recipes.chunked(RECIPES_IN_THE_ROW)) { recipes ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (recipe in recipes) {
                        RecipeItem(
                            modifier = Modifier
                                .weight(1f),
                            name = recipe.name,
                            onClick = { onRecipeClicked(recipe) }
                        )
                    }
                }
            }
            item { 
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        RecipesByCategoryState.NotShown -> Unit
    }
}

@Composable
fun RecipeItem(
    modifier: Modifier = Modifier,
    name: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .foodLibShadow()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .background(color = FoodLibTheme.colorScheme.surface)
            .padding(16.dp)
            .sizeIn(
                minHeight = 160.dp,
                maxHeight = 160.dp
            )
    ) {
        LargeBodyText(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            text = name,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W300
        )
    }
}

fun LazyListScope.searchBar(
    onClick: () -> Unit
) {
    item {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .foodLibShadow()
                .background(
                    color = FoodLibTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(horizontal = 12.dp)
                .clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = MutableInteractionSource()
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Rounded.Search,
                tint = FoodLibTheme.colorScheme.secondary,
                contentDescription = null
            )
            LargeBodyText(
                text = stringResource(id = R.string.search_hint),
                color = FoodLibTheme.colorScheme.textSecondary
            )
        }
    }
}

fun LazyListScope.categories(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(start = 16.dp, end = 16.dp),
    onClick: (Category) -> Unit
) {
    item {
        MediumHeadLineText(text = stringResource(id = R.string.categories))
    }
    item {
        var selectedItem by remember { mutableStateOf(Category.ALL) }

        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = contentPadding
        ) {
            items(
                items = Category.values(),
                key = { it.name }
            ) { category ->
                val selected = category == selectedItem
                val backgroundColor by animateColorAsState(
                    targetValue = if (selected)
                        FoodLibTheme.colorScheme.primary
                    else
                        FoodLibTheme.colorScheme.surface
                )
                val textColor by animateColorAsState(
                    targetValue = if (!selected)
                        FoodLibTheme.colorScheme.primary
                    else
                        FoodLibTheme.colorScheme.surface
                )
                val interactionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .height(52.dp)
                        .foodLibShadow()
                        .background(
                            color = backgroundColor,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {
                                selectedItem = category
                                onClick(category)
                            }
                        )
                ) {
                    LargeBodyText(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(id = category.displayName),
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}