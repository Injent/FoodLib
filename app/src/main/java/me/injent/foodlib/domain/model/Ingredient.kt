package me.injent.foodlib.domain.model

import kotlinx.serialization.Serializable
import me.injent.foodlib.util.Searchable

@Serializable
data class Ingredient(
    val name: String,
    val amount: Int = 0,
    val metric: String
) : Searchable {
    val avalaibleMetrics: List<String> = metric.split('/')

    override fun doesMatchQuery(query: String): Boolean {
        return if (name.contains(" ")) {
            val matchingCombinations = name.split(" ")

            matchingCombinations.any {
                it.contains(query, ignoreCase = true)
            }
        } else {
            name.contains(query, ignoreCase = true)
        }
    }
}