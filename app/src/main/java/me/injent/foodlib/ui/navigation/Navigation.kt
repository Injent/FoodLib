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

package me.injent.foodlib.ui.navigation

import androidx.navigation.NavController
import me.injent.foodlib.ui.navigation.FoodLibDestinations.BROWSE_ROUTE
import me.injent.foodlib.ui.navigation.FoodLibDestinations.HOME_ROUTE
import me.injent.foodlib.ui.navigation.FoodLibNavigationArgs.DRAFT_ID
import me.injent.foodlib.ui.navigation.FoodLibNavigationArgs.RECIPE_ID
import me.injent.foodlib.ui.navigation.FoodLibScreens.BROWSE
import me.injent.foodlib.ui.navigation.FoodLibScreens.DRAFT
import me.injent.foodlib.ui.navigation.FoodLibScreens.HOME
import me.injent.foodlib.ui.navigation.FoodLibScreens.RECIPE

private object FoodLibScreens {
    const val HOME = "home"
    const val BROWSE = "browse"
    const val RECIPE = "recipe"
    const val DRAFT = "draft"
    const val CATEGORY = "category"
}

object FoodLibNavigationArgs {
    const val RECIPE_ID = "recipeId"
    const val DRAFT_ID = "draftId"
    const val CATEGORY_ID = "categoryId"
}

object FoodLibDestinations {
    const val HOME_ROUTE = HOME
    const val BROWSE_ROUTE = BROWSE
    const val RECIPE_ADD_ROUTE = "$RECIPE/{$RECIPE_ID}/add"
    const val RECIPE_EDIT_ROUTE = "$RECIPE/{$RECIPE_ID}/edit"
    const val RECIPE_DETAILS_ROUTE = "$RECIPE/{$RECIPE_ID}/details"
    const val DRAFT_EDIT_ROUTE = "$DRAFT/{$DRAFT_ID}"
}

class NavActions(private val navController: NavController) {
    fun navigateToHome() {
        navController.navigate(HOME_ROUTE)
    }
    fun navigateToBrowser() {
        navController.navigate(BROWSE_ROUTE)
    }
    fun navigateToRecipeEdit(recipeId: Long) {
        navController.navigate("$RECIPE/$recipeId/edit")
    }
    fun navigateToRecipeDetails(recipeId: Long) {
        navController.navigate("$RECIPE/$recipeId/details")
    }
    fun navigateToDraftEdit(draftId: String) {
        navController.navigate("$DRAFT/$draftId")
    }
    fun navigateToRecipeAdd() {
        navController.navigate("$RECIPE/-1/add")
    }
}