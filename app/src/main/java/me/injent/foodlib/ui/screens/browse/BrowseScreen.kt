package me.injent.foodlib.ui.screens.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.injent.foodlib.R
import me.injent.foodlib.data.local.database.RecipeSearchEntity
import me.injent.foodlib.ui.components.FoodLibSearchBar
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
            onValueChanged = viewModel::setSearchText,
            focusedOnInit = true,
            onDismiss = navActions::navigateToHome
        )
        BrowseList(
            isLoading = uiState.isLoading,
            recipes = uiState.filteredRecipes,
            onRecipeSelected = { navActions.navigateToRecipeDetails(it) }
        )
    }
}

@Composable
fun BrowseList(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    recipes: List<RecipeSearchEntity>,
    onRecipeSelected: (id: Long) -> Unit
) {
    if (isLoading) Text(text = "Loading...")
    LazyColumn(
        modifier = modifier
    ) {
        items(recipes) { recipe ->
            RecipeItem(
                name = recipe.name,
                onClick = { onRecipeSelected(recipe.id) },
                contentColor = FoodLibTheme.colorScheme.textPrimary
            )
        }
    }
}

@Composable
private fun RecipeItem(
    name: String,
    contentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Rounded.Search,
                tint = contentColor,
                contentDescription = null
            )
            Text(
                modifier = Modifier.weight(1f),
                text = name,
                color = contentColor,
                textAlign = TextAlign.Start
            )
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Rounded.KeyboardArrowRight,
                tint = contentColor,
                contentDescription = null
            )
        }
    }
}