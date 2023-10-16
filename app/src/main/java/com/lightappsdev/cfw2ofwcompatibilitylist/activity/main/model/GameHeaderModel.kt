package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model

import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums.GameAdapterTypes

data class GameHeaderModel(
    val header: String,
    override val viewType: GameAdapterTypes = GameAdapterTypes.HEADER
) : GameAdapterModel()
