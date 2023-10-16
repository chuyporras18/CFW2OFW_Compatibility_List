package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.storage.StorageReference
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.interfaces.GamesDao
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameWithIdsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.ByteArrayOutputStream
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class GameDataProvider @Inject constructor(
    private val gamesDao: GamesDao,
    private val storageReference: StorageReference
) {

    companion object {
        private const val BASE_URL: String = "https://www.gametdb.com/PS3/"
        private const val COMPRESS_THRESHOLD: Long = 60L * 1024
    }

    suspend fun fetchGameData(gameWithIdsEntity: GameWithIdsEntity) {
        try {
            withContext(Dispatchers.IO) {
                gameWithIdsEntity.gameIdEntity.flatMap { gameIdEntity ->
                    gameIdEntity.gameCountryCode.split(" ")
                }.forEach { gameCountryCode ->
                    val document =
                        Jsoup.connect(BASE_URL + gameCountryCode)
                            .userAgent("Mozilla")
                            .timeout(60 * 1000).get()

                    val text = document.getElementById("wikitext")?.text().orEmpty()

                    val images = document.getElementsByClass("highslide")
                        .map { element -> element.attr("href") }

                    val urls = images.mapIndexedNotNull { index, s ->
                        val byteArray = URL(s).readBytes()
                        val bitmap =
                            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                        val filename = "${gameCountryCode}_${index + 1}.jpg"

                        var quality = 100
                        var outputArray: ByteArray

                        if (byteArray.size <= COMPRESS_THRESHOLD) {
                            return@mapIndexedNotNull null
                        }

                        do {
                            ByteArrayOutputStream().use { outputStream ->
                                bitmap.compress(
                                    Bitmap.CompressFormat.JPEG,
                                    quality,
                                    outputStream
                                )
                                outputArray = outputStream.toByteArray()
                                quality -= (quality * 0.05).roundToInt()
                            }
                        } while (outputArray.size > COMPRESS_THRESHOLD && quality >= 80)

                        val reference =
                            storageReference.child("CFW2OFW/game_covers/${gameCountryCode}/${filename}")
                        try {
                            reference.putBytes(outputArray).await()
                            return@mapIndexedNotNull reference.downloadUrl.await().toString()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            println("$gameCountryCode not uploaded $filename")
                            return@mapIndexedNotNull null
                        }
                    }

                    if (text.contains("doesn't exist") || images.isEmpty() || images.size != urls.size) {
                        return@forEach
                    }

                    updateGameImages(gameWithIdsEntity.gameEntity.id, images)

                    return@withContext
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println(gameWithIdsEntity.gameEntity.title)
        }
    }

    suspend fun getPendingDataGames(): List<GameWithIdsEntity> {
        return withContext(Dispatchers.IO) {
            gamesDao.getAllGames().filter { it.gameEntity.images.isEmpty() }
                .sortedBy { it.gameEntity.title }
        }
    }

    private suspend fun updateGameImages(id: Int, images: List<String>) {
        withContext(Dispatchers.IO) {
            gamesDao.updateGameImages(id, images)
        }
    }
}