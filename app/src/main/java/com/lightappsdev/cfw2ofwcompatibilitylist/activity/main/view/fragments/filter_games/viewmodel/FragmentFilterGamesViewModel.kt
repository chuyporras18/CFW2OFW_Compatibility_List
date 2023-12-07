package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.filter_games.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.core.GameIdsProvider
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums.GameFiltersType
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums.GameWorkTypes
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.filter_games.enums.SortGamesTypes
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.viewmodel.MainActivityViewModel
import com.lightappsdev.cfw2ofwcompatibilitylist.cfw2OfwApp
import com.lightappsdev.cfw2ofwcompatibilitylist.fromJson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FragmentFilterGamesViewModel @Inject constructor(val gameIdsProvider: GameIdsProvider) : ViewModel() {

    private val _sortGamesTypes: MutableLiveData<SortGamesTypes> = MutableLiveData()
    val sortGamesTypes: LiveData<SortGamesTypes> = _sortGamesTypes

    private val _checkedFilter: MutableLiveData<BooleanArray> = MutableLiveData()
    val checkedFilter: LiveData<BooleanArray> = _checkedFilter

    private val _gameCheckedFilter: MutableLiveData<BooleanArray> = MutableLiveData()
    val gameCheckedFilter: LiveData<BooleanArray> = _gameCheckedFilter

    private val _gameIdsCheckedFilter: MutableLiveData<BooleanArray> = MutableLiveData()
    val gameIdsCheckedFilter: LiveData<BooleanArray> = _gameIdsCheckedFilter

    private val _allGamesCheckedFilter: MutableLiveData<Boolean> = MutableLiveData(false)
    val allGamesCheckedFilter: LiveData<Boolean> = _allGamesCheckedFilter

    fun getSortGameType(): SortGamesTypes {
        return cfw2OfwApp.preferences.getString(MainActivityViewModel.SORT_GAME_TYPES_KEY, null)
            ?.let { s -> SortGamesTypes.valueOf(s) } ?: SortGamesTypes.ABC_ASC
    }

    fun getGameWorkCheckedFilters(): BooleanArray {
        val filterGamesTypes = GameWorkTypes.values()

        val booleanArray =
            cfw2OfwApp.preferences.getString(MainActivityViewModel.CHECKED_FILTERS_KEY, null)
                ?.fromJson<BooleanArray>() ?: BooleanArray(filterGamesTypes.size) { true }

        _checkedFilter.value = booleanArray

        return booleanArray
    }

    fun getGameIdsCheckedFilters(): BooleanArray {
        val filterGameIds = gameIdsProvider.generateGameIds()

        val booleanArray = cfw2OfwApp.preferences.getString(
            MainActivityViewModel.GAME_IDS_CHECKED_FILTER_KEY,
            null
        )?.fromJson<BooleanArray>() ?: BooleanArray(filterGameIds.size) { true }

        _gameIdsCheckedFilter.value = booleanArray

        return booleanArray
    }

    fun getGameCheckedFilters(): BooleanArray {
        val filterGamesTypes = GameFiltersType.values()

        val booleanArray =
            cfw2OfwApp.preferences.getString(MainActivityViewModel.GAME_CHECKED_FILTERS_KEY, null)
                ?.fromJson<BooleanArray>() ?: BooleanArray(filterGamesTypes.size) { true }

        _gameCheckedFilter.value = booleanArray

        return booleanArray
    }

    fun getAllGamesFilterChecked(): Boolean {
        return cfw2OfwApp.preferences
            .getBoolean(MainActivityViewModel.ALL_GAMES_CHECKED_FILTER_KEY, true)
    }

    fun toggleAllGamesCheckedFilter() {
        _allGamesCheckedFilter.value = !(_allGamesCheckedFilter.value ?: false)
    }

    fun sortGameTypes(sortGamesTypes: SortGamesTypes) {
        _sortGamesTypes.value = sortGamesTypes
    }

    fun checkedFilters(booleanArray: BooleanArray) {
        _checkedFilter.value = booleanArray
    }

    fun gameIdsCheckedFilter(booleanArray: BooleanArray) {
        _gameIdsCheckedFilter.value = booleanArray
    }

    fun gameCheckedFilters(booleanArray: BooleanArray) {
        _gameCheckedFilter.value = booleanArray
    }

    fun allGamesCheckedFilter(b: Boolean) {
        _allGamesCheckedFilter.value = b
    }
}