package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameEntity
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameIdEntity
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameModel
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameWithIdsEntity
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.toEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GamesDao {

    @Query("SELECT * FROM GAMES_TABLE ORDER BY title ASC")
    fun getGames(): Flow<List<GameWithIdsEntity>>

    @Query("SELECT * FROM GAMES_TABLE")
    suspend fun getAllGames(): List<GameWithIdsEntity>

    @Query("SELECT * FROM GAMES_TABLE WHERE id == :id")
    suspend fun getGameById(id: Int): GameWithIdsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(gameEntity: GameEntity)

    @Transaction
    suspend fun insertGame(gameModel: GameModel) {
        insertGame(gameModel.toEntity())
        insertGameIds(gameModel.ids.map { gameIdModel -> gameIdModel.toEntity() })
    }

    @Transaction
    suspend fun insertGames(gameModels: List<GameModel>) {
        gameModels.forEach { gameModel -> insertGame(gameModel) }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameIds(gameIdEntity: List<GameIdEntity>)

    @Query("UPDATE GAMES_TABLE SET images = :images WHERE id == :id")
    suspend fun updateGameImages(id: Int, images: List<String>)

    @Query("UPDATE GAMES_TABLE SET platinum = :b WHERE id == :id")
    suspend fun updatePlatinumGame(b: Boolean, id: Int)
}