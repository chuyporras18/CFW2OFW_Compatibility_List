package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import javax.inject.Singleton

@Singleton
class GameRoomConverter {

    private val gson: Gson = Gson()

    @TypeConverter
    fun toJson(any: Any): String {
        return gson.toJson(any)
    }

    @TypeConverter
    fun jsonToStrings(json: String): List<String> {
        return gson.fromJson(json, Array<String>::class.java).toList()
    }
}