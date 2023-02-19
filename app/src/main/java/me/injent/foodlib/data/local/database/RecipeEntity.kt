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

package me.injent.foodlib.data.local.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.injent.foodlib.domain.model.Category
import me.injent.foodlib.domain.model.Ingredient

@Entity(tableName = "recipes")
data class RecipeEntity(
    val name: String,
    val content: String,
    @TypeConverters(IngredientsTypeConverter::class)
    val ingredients: List<Ingredient>? = null,
    val category: Category = Category.ALL
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    @TypeConverters(InstantConverter::class)
    @ColumnInfo(name = "last_used_date")
    var lastUsedDate: Instant = Clock.System.now()
}

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY id DESC LIMIT 10")
    fun getRecipes(): Flow<List<RecipeEntity>>
    @Query("SELECT * FROM recipes ORDER BY last_used_date DESC LIMIT :limit")
    fun getRecentRecipesWithoutContent(limit: Int): Flow<List<RecipeEntity>>
    @Query("SELECT * FROM recipes WHERE id =:id LIMIT 1")
    suspend fun findRecipe(id: Long): RecipeEntity
    @Query("SELECT id, name FROM recipes WHERE name LIKE '%' || :query || '%' LIMIT 4")
    suspend fun findRecipesByName(query: String): List<RecipeSearchEntity>
    @Insert
    suspend fun insertRecipe(item: RecipeEntity): Long
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRecipe(item: RecipeEntity)
    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipe(id: Long)

    @Query("SELECT * FROM recipes WHERE category = :category OR :category = 'ALL' LIMIT :limit")
    suspend fun findByCategory(category: String, limit: Int): List<RecipeEntity>
}
