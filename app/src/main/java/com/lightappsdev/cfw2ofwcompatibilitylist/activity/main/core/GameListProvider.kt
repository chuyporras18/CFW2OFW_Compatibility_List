package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.core

import com.google.firebase.storage.StorageReference
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums.GameWorkTypes
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.interfaces.GamesDao
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameIdModel
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameModel
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameWithIdsEntity
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.toDomain
import com.lightappsdev.cfw2ofwcompatibilitylist.cfw2OfwApp
import com.lightappsdev.cfw2ofwcompatibilitylist.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameListProvider @Inject constructor(
    private val gamesDao: GamesDao,
    private val storageReference: StorageReference
) {

    companion object {
        private const val BASE_URL: String =
            "https://www.psdevwiki.com/ps3/CFW2OFW_Compatibility_List"
    }

    val gameList: Flow<List<GameWithIdsEntity>> = gamesDao.getGames().flowOn(Dispatchers.IO)

    suspend fun getListFromRepository(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val byteArray = storageReference.child("CFW2OFW/game_backup.json")
                    .getBytes(Long.MAX_VALUE).await()

                val map = String(byteArray).fromJson<Map<String, List<String>>>()

                val games: MutableMap<String, MutableList<GameIdModel>> = mutableMapOf()

                val document = Jsoup.connect(BASE_URL).userAgent("Mozilla").timeout(60 * 1000).get()

                val tables =
                    document.getElementsByClass("wikitable mw-datatable sortable")

                tables.forEach { table ->
                    val tbodies = table.getElementsByTag("tbody")

                    tbodies.forEach { tbody ->
                        val trs = tbody.getElementsByTag("tr").toMutableList()

                        var lastKey: String? = null
                        trs.forEach trs@{ tr ->
                            val tds = tr.getElementsByTag("td").toMutableList()

                            if (tds.isEmpty()) {
                                return@trs
                            }

                            if (tds.size == 5) {
                                lastKey = tds.removeFirst().text()

                                games.getOrPut(lastKey!!) { mutableListOf() }
                            }

                            lastKey?.let { key ->
                                val id = tds[0].text()

                                if (games[key]?.any { gameIdModel -> gameIdModel.id == id } == true) {
                                    return@let
                                }

                                val gameId = lastKey!!.hashCode()
                                val worksByDTU =
                                    GameWorkTypes.values().find { tds[1].text() in it.title }!!
                                val worksByHAN =
                                    GameWorkTypes.values().find { tds[2].text() in it.title }!!
                                val notes = tds[3].text()

                                val gameIdModel =
                                    GameIdModel(id, gameId, worksByDTU, worksByHAN, notes)
                                games[key]!!.add(gameIdModel)
                            }
                        }
                    }
                }

                saveListToDatabase(games.map { (key, value) ->
                    val keys = value.flatMap { gameIdModel -> gameIdModel.id.split(" ") }
                    val images = keys.mapNotNull { k -> map[k] }.flatten()
                    GameModel(key, images = images, ids = value)
                })

                cfw2OfwApp.preferences.edit().putBoolean("fetch_list", false).apply()

                return@withContext true
            }
        } catch (e: Exception) {
            e.printStackTrace()

            return false
        }
    }

    private suspend fun saveListToDatabase(list: List<GameModel>) {
        withContext(Dispatchers.IO) {
            gamesDao.insertGames(list)
        }
    }

    suspend fun getAllGames(): List<GameModel> {
        return withContext(Dispatchers.IO) {
            gamesDao.getAllGames().map { gameWithIdsEntity ->
                gameWithIdsEntity.gameEntity.toDomain()
                    .copy(ids = gameWithIdsEntity.gameIdEntity.map { gameIdEntity -> gameIdEntity.toDomain() })
            }
        }
    }

    suspend fun getGameById(id: Int): GameModel? {
        return withContext(Dispatchers.IO) {
            val entity = gamesDao.getGameById(id)
            entity?.gameEntity?.toDomain()
                ?.copy(ids = entity.gameIdEntity.map { it.toDomain() })
        }
    }

    suspend fun updateGamePlatinum(b: Boolean, id: Int) {
        withContext(Dispatchers.IO) {
            gamesDao.updatePlatinumGame(b, id)
        }
    }
}