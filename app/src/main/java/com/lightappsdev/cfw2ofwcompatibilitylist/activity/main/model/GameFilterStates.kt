package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model

import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums.GameFiltersType
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums.GameWorkTypes
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.filter_games.enums.SortGamesTypes

data class GameFilterStates(
    val querySearch: String? = null,
    val sortGamesTypes: SortGamesTypes? = null,
    val workFilters: List<GameWorkTypes>? = null,
    val gameFilters: List<GameFiltersType>? = null,
    val allGamesChecked: Boolean = true,
    val gameIdsFilters: Set<String>? = null
)
