package me.injent.foodlib.data.local.datastore

import androidx.datastore.core.DataStore
import javax.inject.Inject

class FoodLibPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {
    val userData = userPreferences.data

    suspend fun setSelectedSection(name: String) {
        userPreferences.updateData {
            it.copy(selectedSection = name)
        }
    }

    suspend fun setLastOpenedRecipeId(draftId: Long) {
        userPreferences.updateData {
            it.copy(lastOpenedRecipeId = draftId)
        }
    }

    suspend fun addDraft(draft: Draft) {
        userPreferences.updateData {
            val drafts = HashMap<String, Draft>(it.drafts)
            drafts[draft.id] = draft
            it.copy(drafts = drafts)
        }
    }

    suspend fun removeDraft(draftId: String) {
        userPreferences.updateData {
            val drafts = HashMap<String, Draft>(it.drafts)
            drafts.remove(draftId)
            it.copy(drafts = drafts)
        }
    }
}