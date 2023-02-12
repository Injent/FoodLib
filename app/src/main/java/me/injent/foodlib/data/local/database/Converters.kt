package me.injent.foodlib.data.local.database

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.injent.foodlib.domain.model.Ingredient

class InstantConverter {
    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let(Instant::fromEpochMilliseconds)

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilliseconds()
}

class IngredientsTypeConverter {
    @TypeConverter
    fun stringToList(value: String): List<Ingredient> {
        return Json.decodeFromString(value)
    }
    @TypeConverter
    fun listToString(value: List<Ingredient>?): String {
        return if(value == null) "" else Json.encodeToString(value)
    }
}