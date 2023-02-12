package me.injent.foodlib.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import me.injent.foodlib.data.local.datastore.UserPreferences
import me.injent.foodlib.data.local.datastore.UserPreferencesSerializer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun userPreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<UserPreferences> =
        DataStoreFactory.create(
            serializer = UserPreferencesSerializer,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        ) {
            context.dataStoreFile("user_preferences.pb")
        }
}