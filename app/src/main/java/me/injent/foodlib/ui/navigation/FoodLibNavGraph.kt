package me.injent.foodlib.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import me.injent.foodlib.ui.navigation.FoodLibDestinations.BROWSE_ROUTE
import me.injent.foodlib.ui.navigation.FoodLibDestinations.DRAFT_EDIT_ROUTE
import me.injent.foodlib.ui.navigation.FoodLibDestinations.HOME_ROUTE
import me.injent.foodlib.ui.navigation.FoodLibDestinations.RECIPE_ADD_ROUTE
import me.injent.foodlib.ui.navigation.FoodLibDestinations.RECIPE_DETAILS_ROUTE
import me.injent.foodlib.ui.navigation.FoodLibDestinations.RECIPE_EDIT_ROUTE
import me.injent.foodlib.ui.navigation.FoodLibNavigationArgs.DRAFT_ID
import me.injent.foodlib.ui.navigation.FoodLibNavigationArgs.RECIPE_ID
import me.injent.foodlib.ui.screens.browse.BrowseScreen
import me.injent.foodlib.ui.screens.home.HomeRoute
import me.injent.foodlib.ui.screens.recipe.details.RecipeDetailsRoute
import me.injent.foodlib.ui.screens.recipe.edit.RecipeEditScreen

@Composable
fun FoodLibNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = HOME_ROUTE,
    navActions: NavActions = remember(navController) {
        NavActions(navController)
    }
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(HOME_ROUTE) {
            HomeRoute(
                onNavigateToBrowse = { navActions.navigateToBrowser() },
                onRecipeClick = { navActions.navigateToRecipeEdit(it) },
                onCreateRecipe = { navActions.navigateToRecipeEdit(-1) },
                onDraftClick = { navActions.navigateToDraftEdit(it) }
            )
        }
        composable(BROWSE_ROUTE) {
            BrowseScreen(navActions = navActions)
        }
        composable(
            route = RECIPE_DETAILS_ROUTE,
            arguments = listOf(navArgument(RECIPE_ID) { type = NavType.StringType })
        ) {
            RecipeDetailsRoute(navActions = navActions)
        }
        composable(
            route = RECIPE_ADD_ROUTE,
            arguments = listOf(navArgument(RECIPE_ID) { type = NavType.StringType })
        ) {
            RecipeEditScreen(
                onBack = { navActions.navigateToHome() }
            )
        }
        composable(
            route = DRAFT_EDIT_ROUTE,
            arguments = listOf(navArgument(DRAFT_ID) { type = NavType.StringType })
        ) {
            RecipeEditScreen(onBack = { navActions.navigateToHome() })
        }
        composable(
            RECIPE_EDIT_ROUTE,
            arguments = listOf(navArgument(RECIPE_ID) { type = NavType.StringType })
        ) {
            RecipeEditScreen(
                onBack = { navActions.navigateToHome() }
            )
        }
    }
}