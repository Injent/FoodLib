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

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.room.*
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

@Immutable
@Entity(tableName = "recipes")
data class Recipe(
    val name: String,
    @TypeConverters(IngredientsTypeConverter::class)
    val ingredients: List<Ingredient>? = null,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @TypeConverters(ZonedDateTimeTypeConverter::class)
    var lastUsed: ZonedDateTime = ZonedDateTime.now()
}

@Immutable
data class Ingredient(
    val name: String,
    val amount: Float = 0f,
    val metric: String
)

object IngredientsTypeConverter : ListTypeConverter<Ingredient>
    (object : TypeToken<List<Ingredient>>() {}.type)

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY id DESC LIMIT 10")
    fun getRecipes(): Flow<List<Recipe>>

    @Query("SELECT name, id, lastUsed FROM recipes LIMIT :limit")
    fun getRecentRecipesWithoutContent(limit: Int): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id =:id LIMIT 1")
    suspend fun findRecipe(id: Int): Recipe

    @Query("SELECT name, id FROM recipes WHERE name LIKE '%' || :query || '%' LIMIT 5")
    suspend fun findRecipesByName(query: String): List<Recipe>

    @Insert
    suspend fun insertRecipe(item: Recipe): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRecipe(item: Recipe)
}
