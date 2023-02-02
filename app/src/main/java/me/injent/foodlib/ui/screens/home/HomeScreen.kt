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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import me.injent.foodlib.R
import me.injent.foodlib.data.local.database.Recipe
import me.injent.foodlib.ui.components.*
import me.injent.foodlib.ui.navigation.NavActions
import me.injent.foodlib.ui.theme.FoodLibTheme
import me.injent.foodlib.util.ignoreHorizontalParentPadding

@Composable
fun HomeScreen(
    contentPaddingValues: PaddingValues = PaddingValues(16.dp),
    navActions: NavActions,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Column(
        Modifier.padding(paddingValues = contentPaddingValues),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FoodLibSearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clickable { navActions.navigateToBrowser() },
            color = FoodLibTheme.colorScheme.surface,
            contentColor = FoodLibTheme.colorScheme.textPrimary,
            placeholder = stringResource(id = R.string.search_hint),
            hintColor = FoodLibTheme.colorScheme.textSecondary.copy(.75f),
            onValueChanged = {}
        )
        Categories(
            modifier = Modifier.ignoreHorizontalParentPadding(16.dp),
            categories = categories,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
            onCategorySelected = { navActions.navigateToCategory(it.name) }
        )
        HeaderText(text = stringResource(id = R.string.recent))
    }
}

// Previews

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    FoodLibTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(FoodLibTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FoodLibSearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                color = FoodLibTheme.colorScheme.surface,
                contentColor = FoodLibTheme.colorScheme.textPrimary,
                shape = RoundedCornerShape(12.dp),
                placeholder = "Искать",
                hintColor = FoodLibTheme.colorScheme.textSecondary.copy(.75f),
                onValueChanged = {}
            )
            Categories(
                modifier = Modifier.ignoreHorizontalParentPadding(16.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                categories = categories,
                onCategorySelected = {}
            )
            HeaderText(text = stringResource(id = R.string.recent))
        }
    }
}