package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games_table")
data class GameEntity(
    val title: String,
    val images: List<String>,
    val platinum: Boolean,
    @PrimaryKey
    val id: Int = title.hashCode(),
)

fun GameEntity.toDomain(): GameModel {
    return GameModel(title, id, images, platinum = platinum)
}