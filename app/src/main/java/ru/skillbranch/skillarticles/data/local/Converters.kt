package ru.skillbranch.skillarticles.data.local

import androidx.room.TypeConverter
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.data.repositories.MarkdownParser
import java.util.*

class DateConverter{
    @TypeConverter
    fun timestampToDate(timestamp: Long): Date = Date(timestamp)

    @TypeConverter
    fun dateTotimestamp(date: Date): Long = date.time
}

class MarkdownConverter{
    @TypeConverter
    fun toMarkdown(content:String?): List<MarkdownElement>? = content?.let { MarkdownParser.parse(it) }
}

class ListConverter {
    companion object {
        const val SPLITTING_SYMBOL = ";"
    }

    @TypeConverter
    fun String?.toListOfStrings(): List<String> =
        this?.split(SPLITTING_SYMBOL)?: emptyList()

    @TypeConverter
    fun List<String>.tostringConverter(): String =
        this.joinToString(separator = SPLITTING_SYMBOL){it}
}