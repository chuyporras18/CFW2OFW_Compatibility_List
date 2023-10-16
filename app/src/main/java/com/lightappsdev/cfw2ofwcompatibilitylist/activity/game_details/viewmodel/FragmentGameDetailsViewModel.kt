package com.lightappsdev.cfw2ofwcompatibilitylist.activity.game_details.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.game_details.adapters.GameImagesAdapter
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.adapters.GameIdAdapter
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.core.GameListProvider
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameModel
import com.lightappsdev.cfw2ofwcompatibilitylist.cfw2OfwApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentGameDetailsViewModel @Inject constructor(private val gameListProvider: GameListProvider) :
    ViewModel() {

    private val _gameModel: MutableLiveData<GameModel?> = MutableLiveData()
    val gameModel: LiveData<GameModel?> = _gameModel

    private val _gameIdAdapter: MutableLiveData<GameIdAdapter> = MutableLiveData()
    val gameIdAdapter: LiveData<GameIdAdapter> = _gameIdAdapter

    private val _gameImagesAdapter: MutableLiveData<GameImagesAdapter> = MutableLiveData()
    val gameImagesAdapter: LiveData<GameImagesAdapter> = _gameImagesAdapter

    fun getGameData(id: Int?) {
        viewModelScope.launch {
            _gameModel.value = id?.let { gameListProvider.getGameById(it) }
        }
    }

    fun copyTitle() {
        viewModelScope.launch {
            val clipboard: ClipboardManager =
                cfw2OfwApp.applicationContext.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Game Title", _gameModel.value?.title)
            clipboard.setPrimaryClip(clip)
        }
    }

    fun togglePlatinum() {
        viewModelScope.launch {
            val id = _gameModel.value?.id ?: return@launch
            val platinum = !(_gameModel.value?.platinum ?: false)
            _gameModel.value = _gameModel.value?.copy(platinum = platinum)
            gameListProvider.updateGamePlatinum(platinum, id)
        }
    }

    fun gameIdAdapter(adapter: GameIdAdapter) {
        _gameIdAdapter.value = adapter
    }

    fun gameImagesAdapter(gameImagesAdapter: GameImagesAdapter) {
        _gameImagesAdapter.value = gameImagesAdapter
    }
}