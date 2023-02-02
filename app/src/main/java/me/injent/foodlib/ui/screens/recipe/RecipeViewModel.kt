package me.injent.foodlib.ui.screens.recipe

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.injent.foodlib.R
import me.injent.foodlib.data.RecipeRepository
import me.injent.foodlib.data.local.database.Ingredient
import me.injent.foodlib.data.local.database.Recipe
import me.injent.foodlib.ui.navigation.FoodLibNavigationArgs.RECIPE_ID
import javax.inject.Inject

data class AddEditRecipeUiState(
    val isLoading: Boolean = false,
    val name: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val isRecipeSaved: Boolean = false,
    val userMessage: Int? = null,
    val totalIngredients: ImmutableList<Ingredient> = persistentListOf()
)

@HiltViewModel
class RecipeViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val recipeRepository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var recipeId: Int = savedStateHandle.get<String>(RECIPE_ID)?.toIntOrNull() ?: -1

    private val _uiState = MutableStateFlow(AddEditRecipeUiState())
    val uiState: StateFlow<AddEditRecipeUiState> = _uiState.asStateFlow()

    init {
        if (recipeId != -1)
            loadRecipe(recipeId)
        else
            createRecipe()
        loadIngredients(context)
    }

    fun saveRecipe() {
        if (uiState.value.name.isEmpty() || uiState.value.ingredients.isEmpty()) {
            _uiState.update {
                it.copy(userMessage = R.string.empty_recipe)
            }
            return
        }
        updateRecipe()
    }

    fun snackbarMessageShown() = _uiState.update { it.copy(userMessage = null) }

    private fun createRecipe() = viewModelScope.launch(Dispatchers.IO) {
        recipeId = recipeRepository.add(uiState.value.name, uiState.value.ingredients).toInt()
        _uiState.update { it.copy(isRecipeSaved = false, isLoading = false) }
    }

    private fun updateRecipe() = viewModelScope.launch(Dispatchers.IO) {
        val recipe = Recipe(
            uiState.value.name,
            uiState.value.ingredients
        ).also {
            it.id = recipeId
        }
        recipeRepository.update(recipe)
        _uiState.update { it.copy(isRecipeSaved = true) }
    }

    private fun loadRecipe(recipeId: Int) {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            recipeRepository.findById(recipeId)?.let { recipe ->
                _uiState.update {
                    it.copy(
                        name = recipe.name,
                        ingredients = recipe.ingredients ?: emptyList(),
                        isLoading = false,
                        isRecipeSaved = true
                    )
                }
            }
        }
    }

    private fun loadIngredients(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val resources = context.resources
            kotlin.runCatching {
                resources.openRawResource(R.raw.ingredients).use {
                    return@runCatching Gson().fromJson<List<Ingredient>>(
                        it.reader(),
                        object : TypeToken<List<Ingredient>>() {}.type
                    )
                }
            }.onSuccess { list ->
                _uiState.update {
                    it.copy(totalIngredients = list.toImmutableList())
                }
            }.onFailure { it.printStackTrace() }
        }
    }

    fun updateName(name: String) = _uiState.update { it.copy(name = name) }

    fun addIngredient(ingredient: Ingredient) {
        _uiState.update { it.copy(ingredients = it.ingredients + ingredient) }
    }

    fun removeIngredient(ingredient: Ingredient) {
        _uiState.update { it.copy(ingredients = it.ingredients - ingredient) }
    }

    fun updateIngredient(ingredient: Ingredient) {
        _uiState.update {
            val current = it.ingredients.find { i -> i.name == ingredient.name }
            val index = it.ingredients.indexOf(current)
            it.copy(
                ingredients = it.ingredients.apply {
                    (this as MutableList<Ingredient>)[index] = ingredient
                }
            )
        }
    }
}