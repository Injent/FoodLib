package me.injent.foodlib.data.local.datastore

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import me.injent.foodlib.domain.model.Category
import me.injent.foodlib.domain.model.Ingredient
import java.util.*

@Serializable
data class Draft(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val content: String = "",
    val category: Category,
    val editDate: Instant = Clock.System.now()
)
