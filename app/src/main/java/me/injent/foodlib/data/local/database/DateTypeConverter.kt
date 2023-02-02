package me.injent.foodlib.data.local.database

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

object ZonedDateTimeTypeConverter {
    @TypeConverter
    fun longToZonedDateTime(value: Long): ZonedDateTime? {
        return ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(value),
            ZoneId.systemDefault()
        )
    }

    @TypeConverter
    fun zonedDateTimeToLong(value: ZonedDateTime?): Long {
        return value?.toEpochSecond() ?: ZonedDateTime.now().toEpochSecond()
    }
}