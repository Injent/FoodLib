package me.injent.foodlib.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.lang.reflect.Type
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

abstract class MapTypeConverter<K, V> constructor(private val type: Type) {
    @OptIn(ExperimentalStdlibApi::class)
    @TypeConverter
    fun stringToMap(value: String): Map<K, V>? {
        return Gson().fromJson(value, type)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @TypeConverter
    fun mapToString(value: Map<K, V>?): String {
        return if(value == null) "" else Gson().toJson(value, type)
    }
}