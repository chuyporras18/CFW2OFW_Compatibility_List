package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameIdsProvider @Inject constructor() {

    fun generateGameIds(): List<String> {
        return listOf(
            "BLUS",
            "BLES",
            "NPUB",
            "NPEB",
            "BLJM",
            "BLJS",
            "BCES",
            "BLAS",
            "BCAS",
            "BLKS",
            "BCUS",
            "BCJS",
            "NPUA",
            "MRTC",
            "BLJB",
            "BCKS",
            "NPJB",
            "NPEA",
            "BLUs"
        ).sorted()
    }
}