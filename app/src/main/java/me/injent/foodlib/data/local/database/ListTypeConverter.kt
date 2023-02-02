package me.injent.foodlib.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.lang.reflect.Type

abstract class ListTypeConverter<T>(private val type: Type) {
    @TypeConverter
    fun stringToList(value: String): List<T> {
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun listToString(value: List<T>?): String {
        return if(value == null) "" else Gson().toJson(value)
    }
}