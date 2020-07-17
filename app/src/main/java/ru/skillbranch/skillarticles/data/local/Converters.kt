package ru.skillbranch.skillarticles.data.local

import androidx.room.TypeConverter
import java.util.*

class DateConverter{
    @TypeConverter
    fun timestampToDate(timestamp: Long): Date = Date(timestamp)

    @TypeConverter
    fun dateTotimestamp(date: Date): Long = date.time


}