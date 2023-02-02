package me.injent.foodlib.ui.screens.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import me.injent.foodlib.R
import me.injent.foodlib.data.local.database.Recipe
import me.injent.foodlib.ui.components.FoodLibSearchBar
import me.injent.foodlib.ui.components.FoodLibSurface
import me.injent.foodlib.ui.navigation.NavActions
import me.injent.foodlib.ui.theme.FoodLibTheme

@Composable
fun BrowseScreen(
    navActions: NavActions,
    contentPaddingValues: PaddingValues = PaddingValues(16.dp),
    viewModel: BrowseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.padding(paddingValues = contentPaddingValues),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FoodLibSearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            color = FoodLibTheme.colorScheme.surface,
            contentColor = FoodLibTheme.colorScheme.textPrimary,
            placeholder = stringResource(id = R.string.search_hint),
            hintColor = FoodLibTheme.colorScheme.textSecondary.copy(.75f),
            onValueChanged = {
                viewModel.setSearchText(it)
            },
            focusedOnInit = true
        )
        BrowseList(
            isLoading = uiState.isLoading,
            recipes = uiState.filteredRecipes.toImmutableList(),
            onRecipeSelected = { navActions.navigateToRecipeEdit(it.id) }
        )
    }
}

@Composable
fun BrowseList(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    recipes: ImmutableList<Recipe>,
    onRecipeSelected: (recipe: Recipe) -> Unit
) {
    if (isLoading) Text(text = "Loading...")
    LazyColumn(
        modifier = modifier
    ) {
        items(recipes) { recipe ->
            RecipeItem(
                recipe = recipe,
                onClick = { onRecipeSelected(recipe) },
                contentColor = FoodLibTheme.colorScheme.textPrimary
            )
        }
    }
}

@Composable
private fun RecipeItem(
    recipe: Recipe,
    contentColor: Color,
    onClick: () -> Unit
) {
    FoodLibSurface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentColor = contentColor,
        color = Color.Transparent
    ) {
        ConstraintLayout(Modifier.fillMaxWidth().padding(12.dp)) {
            val (leadingIcon, text, trailingIcon) = createRefs()

            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                modifier = Modifier.constrainAs(leadingIcon) {
                    start.linkTo(parent.start)
                }
            )
            Text(
                text = recipe.name,
                modifier = Modifier.constrainAs(text) {
                    start.linkTo(leadingIcon.end, 12.dp)
                },
                textAlign = TextAlign.Start,
            )
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.constrainAs(trailingIcon) {
                    end.linkTo(parent.end)
                }
            )
        }
    }
}

@Preview
@Composable
fun RecipePreview() {
    FoodLibTheme {
        val list = persistentListOf(
            Recipe("Свинная корочка"),
            Recipe("Собака отварная"),
            Recipe("Вода кипяченая"),
            Recipe("Алег отбивной"),
            Recipe("Платон")
        )
        BrowseList(
            isLoading = false,
            recipes = list,
            onRecipeSelected = {}
        )
    }
}