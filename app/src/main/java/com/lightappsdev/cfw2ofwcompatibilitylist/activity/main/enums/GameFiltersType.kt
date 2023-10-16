package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums

import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameModel

enum class GameFiltersType(
    val title: String,
    val filterMessage: String,
    val filterPredicate: (GameModel) -> Boolean
) {
    PLATINUM(
        title = "Platino",
        filterMessage = "Platino Obtenido",
        filterPredicate = { gameModel -> gameModel.platinum }),
    NOT_PLATINUM(
        title = "Sin Platino",
        filterMessage = "Platino No Obtenido",
        filterPredicate = { gameModel -> !gameModel.platinum })
}