package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model

import com.lightappsdev.cfw2ofwcompatibilitylist.R
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums.GameWorkTypes

data class GameIdModel(
    val id: String,
    val gameId: Int,
    val worksByDTU: GameWorkTypes,
    val worksByHAN: GameWorkTypes,
    val notes: String
) {

    fun getDtuIcon(): Int {
        return getWorkIcon(worksByDTU)
    }

    fun getHanIcon(): Int {
        return getWorkIcon(worksByHAN)
    }

    private fun getWorkIcon(gameWorkTypes: GameWorkTypes): Int {
        return when (gameWorkTypes) {
            GameWorkTypes.YES -> R.drawable.baseline_check_24
            GameWorkTypes.NO -> R.drawable.baseline_close_24
            GameWorkTypes.UNKNOWN -> R.drawable.baseline_question_mark_24
            GameWorkTypes.EXCLUSIVE_METHOD -> R.drawable.baseline_swap_horiz_24
        }
    }
}

fun GameIdModel.toEntity(): GameIdEntity {
    return GameIdEntity(id, gameId, worksByDTU.title.first(), worksByHAN.title.first(), notes)
}