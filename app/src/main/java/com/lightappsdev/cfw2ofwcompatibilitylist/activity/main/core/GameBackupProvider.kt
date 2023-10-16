package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.core

import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameBackupProvider @Inject constructor(
    private val gameListProvider: GameListProvider,
    private val storageReference: StorageReference
) {

    suspend fun gameImagesBackup(): Boolean {
        return withContext(Dispatchers.IO) {
            val games = gameListProvider.getAllGames()

            val imagesMap = games.flatMap { gameModel ->
                gameModel.ids.flatMap { it.id.split(" ") }
                    .map { gameIdModel -> gameIdModel to gameModel.images }
            }.toMap()

            val json = Gson().toJson(imagesMap)

            try {
                val reference = storageReference.child("CFW2OFW/game_backup.json")

                reference.putBytes(json.toByteArray()).await()

                return@withContext true
            } catch (e: Exception) {
                return@withContext false
            }
        }
    }
}