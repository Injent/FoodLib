package me.injent.foodlib.data.local.datastore

import kotlinx.serialization.Serializable
import me.injent.foodlib.ui.navigation.FoodLibDestinations.HOME_ROUTE

@Serializable
data class UserPreferences(
    val selectedSection: String = HOME_ROUTE,
    val lastOpenedRecipeId: Long? = null,
    val drafts: Map<String, Draft> = emptyMap()
)