package me.injent.foodlib.ui.screens.recipe.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    recipeRepository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recipeId = savedStateHandle.get<String>(RECIPE_ID)?.toLong() ?: throw IllegalStateException("recipeId cannot be null")

    val recipeUiState = flow<RecipeDetailsUiState> {
        val recipeEntity = recipeRepository.findById(recipeId)

        if (recipeEntity == null) {
            throw RuntimeException("Illegal recipe state")
        } else {
            emit(RecipeDetailsUiState.Success(Recipe(recipeEntity)))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecipeDetailsUiState.Loading
    )

    fun getShareMessage(): String? {
        val state = recipeUiState.value
        if (state is RecipeDetailsUiState.Success) {

        }
        return null
    }
}