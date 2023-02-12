package me.injent.foodlib.ui.screens.recipe.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import me.injent.foodlib.data.RecipeRepository
import me.injent.foodlib.domain.model.Recipe
import me.injent.foodlib.ui.navigation.FoodLibNavigationArgs.RECIPE_ID
import javax.inject.Inject

sealed interface RecipeDetailsUiState {
    object Loading : RecipeDetailsUiState
    data class Success(
        val recipe: Recipe
    ) : RecipeDetailsUiState
}

class RecipeDetailsViewModel @Inject constructor(
    recipeRepository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val recipeUiState = flow<RecipeDetailsUiState> {
        val recipeId = savedStateHandle.get<String>(RECIPE_ID)?.toLongOrNull()
            ?: throw RuntimeException("Illegal recipe state")

        val recipeEntity = recipeRepository.findById(recipeId)

        if (recipeEntity == null) {
            throw RuntimeException("Illegal recipe state")
        } else {
            RecipeDetailsUiState.Success(Recipe(recipeEntity))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecipeDetailsUiState.Loading
    )


}