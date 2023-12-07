package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.viewmodel

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lightappsdev.cfw2ofwcompatibilitylist.R
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.adapters.GameAdapter
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.core.GameBackupProvider
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.core.GameDataProvider
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.core.GameIdsProvider
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.core.GameListProvider
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums.GameAdapterTypes
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums.GameFiltersType
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums.GameWorkTypes
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameAdapterModel
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameFilterStates
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameHeaderModel
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameIdEntity
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameWithIdsEntity
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.UpdateGamesState
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.toDomain
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.MainActivity
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.filter_games.enums.SortGamesTypes
import com.lightappsdev.cfw2ofwcompatibilitylist.cfw2OfwApp
import com.lightappsdev.cfw2ofwcompatibilitylist.fromJson
import com.lightappsdev.cfw2ofwcompatibilitylist.toJson
import com.lightappsdev.cfw2ofwcompatibilitylist.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val gameListProvider: GameListProvider,
    private val gameDataProvider: GameDataProvider,
    private val gameBackupProvider: GameBackupProvider,
    private val gameIdsProvider: GameIdsProvider
) : ViewModel() {

    companion object {
        const val CHECKED_FILTERS_KEY: String = "checked_filters"
        const val GAME_CHECKED_FILTERS_KEY: String = "game_checked_filters"
        const val SORT_GAME_TYPES_KEY: String = "sort_game_types"
        const val ALL_GAMES_CHECKED_FILTER_KEY: String = "all_games_checked_filter"
        const val GAME_IDS_CHECKED_FILTER_KEY: String = "game_ids_checked_filters"
    }

    private val _gameFilterStates: MutableStateFlow<GameFilterStates> =
        MutableStateFlow(GameFilterStates())
    val gameFilterStates: StateFlow<GameFilterStates> = _gameFilterStates

    val gameList: Flow<List<GameAdapterModel>> =
        combine(
            gameListProvider.gameList,
            gameFilterStates
        ) { list, gameFilterStates ->
            val map = mutableMapOf<String, MutableList<GameAdapterModel>>()

            when (gameFilterStates.sortGamesTypes) {
                SortGamesTypes.ABC_ASC -> list.sortedBy { gameWithIdsEntity -> gameWithIdsEntity.gameEntity.title }
                SortGamesTypes.ABC_DESC -> list.sortedByDescending { gameWithIdsEntity -> gameWithIdsEntity.gameEntity.title }
                null -> list
            }.forEach { gameWithIdsEntity ->

                if (!isQuerySearchValid(gameFilterStates.querySearch, gameWithIdsEntity)) {
                    return@forEach
                }

                if (!gameFilterStates.allGamesChecked) {
                    val isValidCheckedFilters =
                        isValidCheckedFilters(gameFilterStates.workFilters, gameWithIdsEntity)
                    val isValidGameCheckedFilters =
                        isValidGameCheckedFilters(gameFilterStates.gameFilters, gameWithIdsEntity)
                    val isValidGameIdsCheckedFilters =
                        isValidGameIdsCheckedFilters(
                            gameFilterStates.gameIdsFilters,
                            gameWithIdsEntity.gameIdEntity
                        )

                    if (!isValidCheckedFilters || !isValidGameCheckedFilters || !isValidGameIdsCheckedFilters) {
                        return@forEach
                    }
                }

                val gameModel = gameWithIdsEntity.gameEntity.toDomain()
                    .copy(ids = gameWithIdsEntity.gameIdEntity.map { gameIdEntity -> gameIdEntity.toDomain() })

                val firstChar = gameModel.title.first().lowercase()
                val key = if (firstChar.isDigitsOnly()) "#" else firstChar.uppercase()

                if (!map.containsKey(key)) {
                    map[key] = mutableListOf(GameHeaderModel(header = key))
                }

                map[key]!!.add(gameModel)
            }

            return@combine map.values.flatten()
        }.flowOn(Dispatchers.IO)

    private val _gameAdapter: MutableLiveData<GameAdapter> = MutableLiveData()
    val gameAdapter: LiveData<GameAdapter> = _gameAdapter

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _updateProgress: MutableStateFlow<UpdateGamesState?> = MutableStateFlow(null)
    val updateProgress: StateFlow<UpdateGamesState?> = _updateProgress

    private val _headers: MutableLiveData<List<String>> = MutableLiveData(emptyList())
    val headers: LiveData<List<String>> = _headers

    init {
        val gameWorkTypes = (cfw2OfwApp.preferences.getString(CHECKED_FILTERS_KEY, null)
            ?.fromJson<BooleanArray>()
            ?: BooleanArray(GameWorkTypes.values().size) { true }).let { booleans ->
            GameWorkTypes.values()
                .filterIndexed { index, _ -> booleans[index] }
        }

        val gameFiltersTypes = (cfw2OfwApp.preferences.getString(GAME_CHECKED_FILTERS_KEY, null)
            ?.fromJson<BooleanArray>()
            ?: BooleanArray(GameFiltersType.values().size) { true }).let { booleans ->
            GameFiltersType.values()
                .filterIndexed { index, _ -> booleans[index] }
        }

        togglePlatinumFilter()

        val sortGamesTypes = cfw2OfwApp.preferences.getString(SORT_GAME_TYPES_KEY, null)
            ?.let { s -> SortGamesTypes.valueOf(s) }
            ?: SortGamesTypes.ABC_ASC.also { sortGamesTypes ->
                cfw2OfwApp.preferences.edit().putString(SORT_GAME_TYPES_KEY, sortGamesTypes.name)
                    .apply()
            }

        val allGamesChecked = cfw2OfwApp.preferences.getBoolean(ALL_GAMES_CHECKED_FILTER_KEY, true)

        val gameIds = gameIdsProvider.generateGameIds()
        val gameIdsCheckedFilter =
            (cfw2OfwApp.preferences.getString(GAME_IDS_CHECKED_FILTER_KEY, null)
                ?.fromJson<BooleanArray>() ?: BooleanArray(gameIds.size) { true }).toList()
                .mapIndexedNotNull { index, b ->
                    if (b) {
                        gameIds[index]
                    } else {
                        null
                    }
                }.toSet()

        _gameFilterStates.value = _gameFilterStates.value.copy(
            sortGamesTypes = sortGamesTypes,
            workFilters = gameWorkTypes,
            gameFilters = gameFiltersTypes,
            allGamesChecked = allGamesChecked,
            gameIdsFilters = gameIdsCheckedFilter
        )
    }

    fun filterDialogResponse(
        sortGamesTypes: SortGamesTypes?,
        booleanArray: BooleanArray?,
        filterBooleanArray: BooleanArray?,
        allGamesCheckedFilter: Boolean?,
        gameIdsSet: Set<String>?
    ) {
        val gameWorkTypes = GameWorkTypes.values()
            .filterIndexed { index, _ -> booleanArray?.getOrNull(index) == true }
        val gameFiltersTypes = GameFiltersType.values()
            .filterIndexed { index, _ -> filterBooleanArray?.getOrNull(index) == true }

        _gameFilterStates.value = _gameFilterStates.value.copy(
            sortGamesTypes = sortGamesTypes,
            workFilters = gameWorkTypes,
            gameFilters = gameFiltersTypes,
            allGamesChecked = allGamesCheckedFilter ?: false,
            gameIdsFilters = gameIdsSet
        )

        togglePlatinumFilter()

        val gameIdsCheckedFilter =
            gameIdsProvider.generateGameIds().map { gameIdsSet.orEmpty().contains(it) }

        cfw2OfwApp.preferences.edit()
            .putString(CHECKED_FILTERS_KEY, booleanArray?.toJson())
            .putString(GAME_CHECKED_FILTERS_KEY, filterBooleanArray?.toJson())
            .putString(SORT_GAME_TYPES_KEY, sortGamesTypes?.name)
            .putBoolean(ALL_GAMES_CHECKED_FILTER_KEY, allGamesCheckedFilter ?: false)
            .putString(GAME_IDS_CHECKED_FILTER_KEY, gameIdsCheckedFilter.toJson())
            .apply()
    }

    fun headerDialogResponse(s: String, mainActivity: MainActivity) {
        viewModelScope.launch {
            gameList.collectLatest { list ->
                val index = list.indexOfFirst { gameAdapterModel ->
                    gameAdapterModel.viewType == GameAdapterTypes.HEADER && (gameAdapterModel as GameHeaderModel).header == s
                }

                if (index == -1) return@collectLatest

                mainActivity.findViewById<RecyclerView>(R.id.recyclerView)?.layoutManager?.let { layoutManager ->
                    (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(index, 0)
                }
            }
        }
    }

    fun getListFromRepository() {
        viewModelScope.launch {
            if (!cfw2OfwApp.preferences.getBoolean("fetch_list", true)) {
                return@launch
            }

            _isLoading.update { true }

            gameListProvider.getListFromRepository()

            _isLoading.update { false }
        }
    }

    fun updateGames() {
        viewModelScope.launch {
            _updateProgress.value = UpdateGamesState()

            gameListProvider.getListFromRepository()

            val pendingGames = gameDataProvider.getPendingDataGames()
            val maxProgress = pendingGames.size

            _updateProgress.value = _updateProgress.value?.copy(max = maxProgress)

            pendingGames.forEachIndexed { index, gameWithIdsEntity ->
                gameDataProvider.fetchGameData(gameWithIdsEntity)
                _updateProgress.value = _updateProgress.value?.copy(progress = index)
            }

            _updateProgress.value = null
        }
    }

    fun backupGames() {
        viewModelScope.launch {
            val result = gameBackupProvider.gameImagesBackup()
            val message = if (result) {
                "Los juegos se han respaldado en la nube."
            } else {
                "No se pudieron respaldar los juegos en la nube."
            }

            toast(message)
        }
    }

    fun gameListAdapter(adapter: GameAdapter) {
        _gameAdapter.value = adapter
    }

    fun querySearch(query: String?) {
        _gameFilterStates.value = _gameFilterStates.value.copy(querySearch = query.orEmpty())
    }

    fun headers(headers: List<String>) {
        _headers.value = headers
    }

    private fun togglePlatinumFilter() {
        _gameFilterStates.value.gameFilters?.let { gameFiltersTypes ->
            if (gameFiltersTypes.contains(GameFiltersType.PLATINUM)) {
                _gameFilterStates.value = _gameFilterStates.value
                    .copy(gameFilters = gameFiltersTypes.minus(GameFiltersType.NOT_PLATINUM))

            } else if (!gameFiltersTypes.contains(GameFiltersType.NOT_PLATINUM)) {
                _gameFilterStates.value = _gameFilterStates.value
                    .copy(gameFilters = gameFiltersTypes.plus(GameFiltersType.NOT_PLATINUM))
            }
        }
    }

    private fun isQuerySearchValid(query: String?, gameWithIdsEntity: GameWithIdsEntity): Boolean {
        val querySearch = query.orEmpty().lowercase().removePrefix(" ").removeSuffix(" ")
        return gameWithIdsEntity.gameEntity.title.lowercase().contains(querySearch) ||
                gameWithIdsEntity.gameIdEntity.any { gameIdEntity ->
                    gameIdEntity.gameCountryCode.lowercase().contains(querySearch)
                }
    }

    private fun isValidCheckedFilters(
        list: List<GameWorkTypes>?,
        gameWithIdsEntity: GameWithIdsEntity
    ): Boolean {
        val gameIdModels =
            gameWithIdsEntity.gameIdEntity.map { gameIdEntity -> gameIdEntity.toDomain() }

        return list.orEmpty().any { gameWorkTypes ->
            gameIdModels.any { gameIdModel -> gameWorkTypes.filterPredicate(gameIdModel) }
        }
    }

    private fun isValidGameCheckedFilters(
        list: List<GameFiltersType>?,
        gameWithIdsEntity: GameWithIdsEntity
    ): Boolean {
        val gameModel = gameWithIdsEntity.gameEntity.toDomain()

        return list.orEmpty().any { gameFiltersType -> gameFiltersType.filterPredicate(gameModel) }
    }

    private fun isValidGameIdsCheckedFilters(
        gameIdsFilters: Set<String>?,
        gameIdEntity: List<GameIdEntity>
    ): Boolean {
        val ids = gameIdEntity.mapNotNull { it.gameCountryCode.chunked(4).firstOrNull() }

        return gameIdsFilters.orEmpty().any { id -> id in ids }
    }
}