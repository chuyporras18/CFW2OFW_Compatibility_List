package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model

import androidx.room.Embedded
import androidx.room.Relation

data class GameWithIdsEntity(
    @Embedded val gameEntity: GameEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "gameId"
    )
    val gameIdEntity: List<GameIdEntity>
)
