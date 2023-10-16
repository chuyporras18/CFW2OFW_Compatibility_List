package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums.GameWorkTypes

@Entity(
    tableName = "game_ids_table",
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GameIdEntity(
    val gameCountryCode: String,
    val gameId: Int,
    val worksByDTU: String,
    val worksByHAN: String,
    val notes: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

fun GameIdEntity.toDomain(): GameIdModel {
    return GameIdModel(
        id = gameCountryCode,
        gameId = gameId,
        worksByDTU = GameWorkTypes.values().find { worksByDTU in it.title }!!,
        worksByHAN = GameWorkTypes.values().find { worksByHAN in it.title }!!,
        notes = notes
    )
}