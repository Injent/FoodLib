package me.injent.foodlib.data

import kotlinx.coroutines.flow.Flow
import me.injent.foodlib.data.local.datastore.Draft
import me.injent.foodlib.data.local.datastore.FoodLibPreferencesDataSource
import me.injent.foodlib.data.local.datastore.UserPreferences
import javax.inject.Inject

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>

    suspend fun setSelectedSection(name: String)
    suspend fun setLastOpenedRecipeId(recipeId: Long)
    suspend fun addDraft(draft: Draft)
    suspend fun removeDraft(draftId: String)
}

class DefaultUserPreferencesRepository @Inject constructor(
    private val userPreferencesDataSource: FoodLibPreferencesDataSource
) : UserPreferencesRepository {
    override val userPreferences = userPreferencesDataSource.userData

    override suspend fun setSelectedSection(name: String) =
        userPreferencesDataSource.setSelectedSection(name)

    override suspend fun setLastOpenedRecipeId(recipeId: Long) =
        userPreferencesDataSource.setLastOpenedRecipeId(recipeId)

    override suspend fun addDraft(draft: Draft) =
        userPreferencesDataSource.addDraft(draft)

    override suspend fun removeDraft(draftId: String) =
        userPreferencesDataSource.removeDraft(draftId)
}