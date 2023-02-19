/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.injent.foodlib.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.injent.foodlib.R
import me.injent.foodlib.domain.model.Category
import me.injent.foodlib.ui.components.*
import me.injent.foodlib.ui.theme.FoodLibIcons
import me.injent.foodlib.ui.theme.FoodLibTheme
import me.injent.foodlib.util.ignoreHorizontalParentPadding

@Composable
fun HomeRoute(
    onNavigateToBrowse: () -> Unit,
    onRecipeClick: (id: Long) -> Unit,
    onCreateRecipe: () -> Unit,
    onDraftClick: (draftId: String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val recipesByCategoryState by viewModel.recipesByCategory.collectAsStateWithLifecycle()
    val draftsUiState by viewModel.draftsUiState.collectAsStateWithLifecycle()

    HomeScreen(
        draftsUiState = draftsUiState,
        filteredRecipes = recipesByCategoryState,
        onSearchBarClick = onNavigateToBrowse,
        onCategoryClick = { viewModel.setCategory(it) },
        onRecipeClick = onRecipeClick,
        onCreateRecipe = onCreateRecipe,
        onDraftClick = onDraftClick
    )
}

@Composable
fun HomeScreen(
    draftsUiState: DraftsUiState,
    filteredRecipes: RecipesByCategoryState,
    onSearchBarClick: () -> Unit,
    onCategoryClick: (Category) -> Unit,
    onRecipeClick: (id: Long) -> Unit,
    onCreateRecipe: () -> Unit,
    onDraftClick: (draftId: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = FoodLibIcons.Menu,
                    tint = FoodLibTheme.colorScheme.secondary,
                    contentDescription = null
                )
                FoodLibIconButton(
                    modifier = Modifier.height(48.dp),
                    onClick = onCreateRecipe,
                    drawable = R.drawable.ic_book,
                    text = stringResource(id = R.string.create)
                )
            }
        }
        item {
            LargeHeadLineText(text = stringResource(id = R.string.menu))
        }
        searchBar(onClick = onSearchBarClick)
        drafts(
            state = draftsUiState,
            onDraftClick = { onDraftClick(it.id) }
        )
        categories(
            modifier = Modifier.ignoreHorizontalParentPadding(16.dp),
            onClick = { category -> onCategoryClick(category) },
        )
        filteredRecipes(
            state = filteredRecipes,
            onRecipeClicked = { recipe -> onRecipeClick(recipe.id) },
        )
    }
}