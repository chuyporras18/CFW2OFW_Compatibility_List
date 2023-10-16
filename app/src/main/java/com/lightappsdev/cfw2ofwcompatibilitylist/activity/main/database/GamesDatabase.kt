package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.converters.GameRoomConverter
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.interfaces.GamesDao
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameEntity
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameIdEntity

@Database(entities = [GameEntity::class, GameIdEntity::class], version = 1)
@TypeConverters(GameRoomConverter::class)
abstract class GamesDatabase : RoomDatabase() {

    abstract fun getGamesDao(): GamesDao
}