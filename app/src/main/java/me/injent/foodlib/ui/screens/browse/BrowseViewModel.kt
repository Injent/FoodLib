package me.injent.foodlib.ui.screens.browse

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.injent.foodlib.data.RecipeRepository
import me.injent.foodlib.data.local.database.Recipe
import me.injent.foodlib.ui.screens.home.HomeUiState
import me.injent.foodlib.util.Async
import javax.inject.Inject

data class BrowseUiState(
    val isLoading: Boolean = false,
    val filteredRecipes: List<Recipe> = emptyList()
)

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _seacrhText = savedStateHandle.getStateFlow("SEARCH_TEXT", "")
    private val _isLoading = MutableStateFlow(false)
    private val _filteredRecipesAsync = combine(_seacrhText, _isLoading) { text, _ ->
        recipeRepository.searchByName(text)
    }
        .map { Async.Success(it) }
        .onStart<Async<List<Recipe>>> { emit(Async.Loading) }

    val uiState = combine(
        _isLoading, _filteredRecipesAsync
    ) { isLoading, recipesAsync ->
        when (recipesAsync) {
            Async.Loading -> {
                BrowseUiState(isLoading = true)
            }
            is Async.Success -> {
                BrowseUiState(
                    filteredRecipes = recipesAsync.data,
                    isLoading = isLoading
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BrowseUiState()
        )

    fun setSearchText(text: String) {
        savedStateHandle["SEARCH_TEXT"] = text
    }
}