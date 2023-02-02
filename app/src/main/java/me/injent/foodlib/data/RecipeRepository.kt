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

package me.injent.foodlib.data

import kotlinx.coroutines.flow.Flow
import me.injent.foodlib.data.local.database.Ingredient
import me.injent.foodlib.data.local.database.Recipe
import me.injent.foodlib.data.local.database.RecipeDao
import me.injent.foodlib.util.RECENT_RECIPES_LIMIT
import javax.inject.Inject

interface RecipeRepository {
    val recipes: Flow<List<Recipe>>
    val recentRecipes: Flow<List<Recipe>>
    suspend fun findById(recipeId: Int): Recipe?
    suspend fun add(name: String, ingredients: List<Ingredient>): Long
    suspend fun update(recipe: Recipe)
    suspend fun searchByName(query: String): List<Recipe>
}

class DefaultRecipeRepository @Inject constructor(
    private val recipeDao: RecipeDao
) : RecipeRepository {

    override val recentRecipes: Flow<List<Recipe>> =
        recipeDao.getRecentRecipesWithoutContent(RECENT_RECIPES_LIMIT)

    override val recipes: Flow<List<Recipe>> =
        recipeDao.getRecipes()

    override suspend fun findById(recipeId: Int) = recipeDao.findRecipe(recipeId)

    override suspend fun add(name: String, ingredients: List<Ingredient>): Long {
        return recipeDao.insertRecipe(Recipe(name, ingredients))
    }

    override suspend fun searchByName(query: String) = recipeDao.findRecipesByName(query)

    override suspend fun update(recipe: Recipe) {
        recipeDao.updateRecipe(recipe)
    }
}
