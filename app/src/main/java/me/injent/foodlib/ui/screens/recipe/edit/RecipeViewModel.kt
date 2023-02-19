package me.injent.foodlib.ui.screens.recipe.edit

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import me.injent.foodlib.R
import me.injent.foodlib.data.RecipeRepository
import me.injent.foodlib.data.UserPreferencesRepository
import me.injent.foodlib.data.local.database.RecipeEntity
import me.injent.foodlib.data.local.datastore.Draft
import me.injent.foodlib.domain.model.Category
import me.injent.foodlib.domain.model.Ingredient
import me.injent.foodlib.ui.navigation.FoodLibNavigationArgs.DRAFT_ID
import me.injent.foodlib.ui.navigation.FoodLibNavigationArgs.RECIPE_ID
import java.util.*
import javax.inject.Inject


data class AddEditRecipeUiState(
    val isLoading: Boolean = false,
    val name: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val content: String = "",
    val category: Category = Category.ALL,
    val isRecipeSaved: Boolean = false,
    val userMessage: Int? = null,
)

@HiltViewModel
class RecipeViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val recipeRepository: RecipeRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    @OptIn(ExperimentalSerializationApi::class)
    private val _ingredientsFullList = flow<List<Ingredient>> {
        kotlin.runCatching {
            context.resources.openRawResource(R.raw.ingredients).use {
                return@runCatching Json.decodeFromStream<List<Ingredient>>(it)
            }
        }.onSuccess {
            emit(it)
        }.onFailure {
            emit(emptyList())
        }
    }

    @OptIn(FlowPreview::class)
    val filteredIngredients = _searchText
        .debounce(500)
        .combine(_ingredientsFullList) { text, ingredients ->
            if (text.isBlank()) {
                ingredients
            } else {
                ingredients.filter {
                    it.doesMatchQuery(text)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private var recipeId = savedStateHandle.get<String>(RECIPE_ID)?.toLongOrNull() ?: -1L
    val isLoadedFromDatabase = recipeId != -1L
    private val draftId = savedStateHandle.get<String>(DRAFT_ID)

    private val _uiState = MutableStateFlow(AddEditRecipeUiState())
    val uiState: StateFlow<AddEditRecipeUiState> = _uiState.asStateFlow()

    private val _currentEditingIngredient = MutableStateFlow<Ingredient?>(null)
    val currentEditingIngredient = _currentEditingIngredient.asStateFlow()

    init {
        if (isLoadedFromDatabase)
            loadRecipe(recipeId)
        else if (draftId != null)
            loadDraft(draftId)
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    /**
     * @return the value of a successful save operation
     */
    fun saveRecipe(): Boolean {
        if (uiState.value.name.isEmpty() || uiState.value.ingredients.isEmpty() || uiState.value.content.isEmpty()) {
            _uiState.update {
                it.copy(userMessage = R.string.empty_draft)
            }
            return false
        }
        updateRecipe()
        if (draftId != null)
            deleteDraft(draftId)
        return true
    }

    fun saveDraft() {
        if (uiState.value.name.isEmpty() && uiState.value.content.isEmpty() && uiState.value.ingredients.isEmpty()) return
        viewModelScope.launch {
            val draft = Draft(
                id = draftId ?: UUID.randomUUID().toString(),
                name = uiState.value.name,
                ingredients = uiState.value.ingredients,
                content = uiState.value.content,
                category = uiState.value.category
            )
            userPreferencesRepository.addDraft(draft)
        }
    }

    fun snackbarMessageShown() = _uiState.update { it.copy(userMessage = null) }

    fun updateContent(content: String) {
        _uiState.update {
            it.copy(
                content = content,
                isRecipeSaved = false
            )
        }
    }

    fun updateName(name: String) = _uiState.update { it.copy(name = name, isRecipeSaved = false) }

    fun addIngredient(ingredient: Ingredient) {
        _uiState.update { it.copy(ingredients = it.ingredients + ingredient, isRecipeSaved = false) }
    }

    fun removeIngredient(name: String) = _uiState.update {
        it.copy(
            ingredients = it.ingredients.filter { item -> item.name != name },
            isRecipeSaved = false
        )
    }

    fun selectCategory(category: Category) = _uiState.update {
        it.copy(
            category = category,
            isRecipeSaved = false
        )
    }

    fun deleteDraftOrRecipe() = viewModelScope.launch {
        if (recipeId != -1L)
            recipeRepository.deleteRecipe(recipeId)
        if (draftId != null) {
            userPreferencesRepository.removeDraft(draftId)
        }
    }

    private fun updateIngredient(ingredient: Ingredient) {
        _uiState.update {
            val current = it.ingredients.find { i -> i.name == ingredient.name }
            val index = it.ingredients.indexOf(current)
            it.copy(
                ingredients = it.ingredients.apply {
                    (this as MutableList<Ingredient>)[index] = ingredient
                },
                isRecipeSaved = false
            )
        }
    }

    fun startIngredientEditing(ingredient: Ingredient) {
        _currentEditingIngredient.value = ingredient
    }

    fun onPhotoPick(bitmap: Bitmap) {

    }

    fun finishIngredientEditing(amount: Int, metric: String) {
        val updatedIngredient = _currentEditingIngredient.value?.copy(
            amount = amount,
            metric = metric
        ) ?: return
        updateIngredient(updatedIngredient)
        _currentEditingIngredient.value = null
    }

    fun finishIngredientEditing() {
        _currentEditingIngredient.value = null
    }

    private fun deleteDraft(draftId: String) = viewModelScope.launch {
        userPreferencesRepository.removeDraft(draftId)
    }

    private fun updateRecipe() = viewModelScope.launch {
        val recipe = RecipeEntity(
            name = uiState.value.name,
            content = uiState.value.content,
            ingredients = uiState.value.ingredients
        ).also {
            it.id = recipeId
        }
        if (isLoadedFromDatabase && draftId == null)
            recipeRepository.update(recipe)
        else
            recipeRepository.add(
                name = uiState.value.name,
                content = uiState.value.content,
                ingredients = uiState.value.ingredients,
                category = uiState.value.category
            )
        _uiState.update { it.copy(isRecipeSaved = true) }
    }

    private fun loadRecipe(recipeId: Long) {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            recipeRepository.findById(recipeId)?.let { recipe ->
                _uiState.update {
                    it.copy(
                        name = recipe.name,
                        ingredients = recipe.ingredients ?: emptyList(),
                        content = recipe.content,
                        isLoading = false,
                        isRecipeSaved = true
                    )
                }
            }
        }
    }

    private fun loadDraft(draftId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            userPreferencesRepository.userPreferences
                .collect {
                    val draft = it.drafts[draftId] ?: return@collect
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            name = draft.name,
                            ingredients = draft.ingredients,
                            content = draft.content,
                            category = draft.category,
                            isRecipeSaved = false
                        )
                    }
                }
        }
    }
}