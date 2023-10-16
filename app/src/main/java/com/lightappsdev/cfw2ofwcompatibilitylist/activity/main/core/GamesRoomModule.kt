package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.core

import android.content.Context
import androidx.room.Room
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.database.GamesDatabase
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.interfaces.GamesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GamesRoomModule {

    private const val GAMES_DATABASE_NAME: String = "games_database"

    @Singleton
    @Provides
    fun providesRoom(@ApplicationContext context: Context): GamesDatabase {
        return Room.databaseBuilder(context, GamesDatabase::class.java, GAMES_DATABASE_NAME).build()
    }

    @Singleton
    @Provides
    fun providesDao(db: GamesDatabase): GamesDao {
        return db.getGamesDao()
    }
}