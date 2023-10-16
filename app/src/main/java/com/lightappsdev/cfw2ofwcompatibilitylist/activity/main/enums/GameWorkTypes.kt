package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums

import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameIdModel

enum class GameWorkTypes(
    val title: List<String>,
    val filterMessage: String,
    val filterPredicate: (GameIdModel) -> Boolean
) {
    YES(
        title = listOf("Yes"),
        filterMessage = "Convertibles",
        filterPredicate = { gameIdModel ->
            gameIdModel.worksByHAN == YES || gameIdModel.worksByDTU == YES
        }),
    NO(
        title = listOf("No", "NO", "No?"),
        filterMessage = "No Convertibles",
        filterPredicate = { gameIdModel ->
            gameIdModel.worksByHAN == NO || gameIdModel.worksByDTU == NO
        }),
    UNKNOWN(
        title = listOf("?"),
        filterMessage = "Sin Probar",
        filterPredicate = { gameIdModel ->
            gameIdModel.worksByHAN == UNKNOWN || gameIdModel.worksByDTU == UNKNOWN
        }),
    EXCLUSIVE_METHOD(
        title = listOf("Exclusive Method"),
        filterMessage = "Con Método Exclusivo",
        filterPredicate = { gameIdModel ->
            gameIdModel.worksByHAN == EXCLUSIVE_METHOD || gameIdModel.worksByDTU == EXCLUSIVE_METHOD
        })
}