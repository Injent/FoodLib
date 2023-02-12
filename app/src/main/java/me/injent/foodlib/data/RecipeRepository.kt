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
import me.injent.foodlib.data.local.database.RecipeDao
import me.injent.foodlib.data.local.database.RecipeEntity
import me.injent.foodlib.domain.model.Category
import me.injent.foodlib.domain.model.Ingredient
import me.injent.foodlib.util.RECENT_RECIPES_LIMIT
import javax.inject.Inject

interface RecipeRepository {
    val recipes: Flow<List<RecipeEntity>>
    val recentRecipes: Flow<List<RecipeEntity>>
    suspend fun findById(recipeId: Long): RecipeEntity?
    suspend fun add(name: String, content: String, ingredients: List<Ingredient>, category: Category): Long
    suspend fun update(recipe: RecipeEntity)
    suspend fun searchByName(query: String): List<RecipeEntity>
    suspend fun deleteRecipe(id: Long)
    suspend fun findByCategory(category: Category, limit: Int): List<RecipeEntity>
}

class DefaultRecipeRepository @Inject constructor(
    private val recipeDao: RecipeDao
) : RecipeRepository {

    override val recentRecipes: Flow<List<RecipeEntity>> =
        recipeDao.getRecentRecipesWithoutContent(RECENT_RECIPES_LIMIT)

    override val recipes: Flow<List<RecipeEntity>> =
        recipeDao.getRecipes()

    override suspend fun findById(recipeId: Long) = recipeDao.findRecipe(recipeId)

    override suspend fun add(name: String, content: String, ingredients: List<Ingredient>, category: Category): Long {
        return recipeDao.insertRecipe(RecipeEntity(name, content, ingredients, category))
    }

    override suspend fun searchByName(query: String) = recipeDao.findRecipesByName(query)

    override suspend fun update(recipe: RecipeEntity) {
        recipeDao.updateRecipe(recipe)
    }

    override suspend fun deleteRecipe(id: Long) {
        recipeDao.deleteRecipe(id)
    }

    override suspend fun findByCategory(category: Category, limit: Int) =
        recipeDao.findByCategory(category.name, limit)
}
