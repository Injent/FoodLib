package me.injent.foodlib.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import me.injent.foodlib.data.local.database.RecipeEntity

@Immutable
@Serializable
data class Recipe(
    val id: Long,
    val name: String,
    val content: String,
    val lastUsedDate: Instant,
    val ingredients: List<Ingredient>
) {
    constructor(recipe: RecipeEntity) : this(
        id = recipe.id,
        name = recipe.name,
        content = recipe.content,
        lastUsedDate = recipe.lastUsedDate,
        ingredients = recipe.ingredients ?: emptyList()
    )
}