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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import me.injent.foodlib.data.RecipeRepository
import me.injent.foodlib.data.UserPreferencesRepository
import me.injent.foodlib.data.local.datastore.Draft
import me.injent.foodlib.domain.model.Category
import me.injent.foodlib.domain.model.Recipe
import me.injent.foodlib.ui.navigation.FoodLibNavigationArgs.CATEGORY_ID
import javax.inject.Inject

sealed interface RecipesByCategoryState {
    object Loading : RecipesByCategoryState
    object NotShown : RecipesByCategoryState
    data class Shown(
        val recipes: List<Recipe>
    ) : RecipesByCategoryState
}


sealed interface DraftsUiState {
    object NotShown : DraftsUiState
    data class Shown(val drafts: List<Draft>) : DraftsUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val savedStateHandle: SavedStateHandle,
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _category = savedStateHandle.getStateFlow(CATEGORY_ID, Category.ALL)

    @OptIn(ExperimentalCoroutinesApi::class)
    val recipesByCategory = _category
        .mapLatest {
            val recipes = recipeRepository.findByCategory(it, 12)
                .map { entity -> Recipe(entity) }
            if (recipes.isNotEmpty())
                RecipesByCategoryState.Shown(recipes)
            else
                RecipesByCategoryState.NotShown
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RecipesByCategoryState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val draftsUiState = userPreferencesRepository.userPreferences
        .mapLatest {
            if (it.drafts.isNotEmpty())
                DraftsUiState.Shown(it.drafts.values.toList())
            else
                DraftsUiState.NotShown
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DraftsUiState.NotShown
        )

    fun setCategory(category: Category) {
        savedStateHandle[CATEGORY_ID] = category
    }
}
