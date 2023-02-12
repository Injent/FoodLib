package me.injent.foodlib.ui.screens.recipe.details

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.injent.foodlib.ui.navigation.NavActions

@Composable
fun RecipeDetailsRoute(
    navActions: NavActions,
    viewModel: RecipeDetailsViewModel = hiltViewModel()
) {
    val recipeUiState by viewModel.recipeUiState.collectAsStateWithLifecycle()
    RecipeDetailsScreen(
        recipeUiState = recipeUiState,
        onBack = {
            navActions.navigateToHome()
        }
    )
}

@Composable
fun RecipeDetailsScreen(
    recipeUiState: RecipeDetailsUiState,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    
    Column {
        IngredientsList(recipeUiState)
    }
}

@Composable
fun IngredientsList(recipeUiState: RecipeDetailsUiState) {
    Row {
        when (recipeUiState) {
            RecipeDetailsUiState.Loading -> {
                CircularProgressIndicator()
            }
            is RecipeDetailsUiState.Success -> {
                //val recipe = remember { recipeUiState.recipe }
                
                Column() {

                }
            }
        }
    }
}