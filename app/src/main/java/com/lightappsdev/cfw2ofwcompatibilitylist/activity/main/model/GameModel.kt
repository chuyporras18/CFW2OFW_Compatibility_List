package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model

import com.lightappsdev.cfw2ofwcompatibilitylist.R
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums.GameAdapterTypes

data class GameModel(
    val title: String,
    val id: Int = title.hashCode(),
    var images: List<String> = emptyList(),
    val ids: List<GameIdModel> = emptyList(),
    val platinum: Boolean = false,
    override val viewType: GameAdapterTypes = GameAdapterTypes.GAME
) : GameAdapterModel() {

    fun gamePlatinumImage(): Int {
        return if (platinum) R.drawable.ps4_platinum_trophy else R.drawable.ps4_locked_trophy
    }
}

fun GameModel.toEntity(): GameEntity {
    return GameEntity(title, images, platinum)
}
