package tv.own.owntv.core.account

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONObject
import tv.own.owntv.core.database.dao.MovieDao
import tv.own.owntv.core.database.dao.ProgressDao
import tv.own.owntv.core.database.dao.SeriesDao
import tv.own.owntv.core.database.dao.SourceDao
import tv.own.owntv.core.database.entity.PlaybackProgressEntity
import tv.own.owntv.core.model.MediaType
import tv.own.owntv.core.model.SourceType
import tv.own.owntv.core.network.HttpClient
import tv.own.owntv.core.settings.SettingsRepository
import tv.own.owntv.core.setup.SourceImporter

private const val TAG = "HanTVCloudSync"
private const val FIREBASE_API_KEY = "AIzaSyCitUUP3TuBwQjZBIjn4fUQX171WFt-2vo"
private const val FIREBASE_PROJECT_ID = "hantv-cloud"

class CloudSyncEngine(
    private val context: android.content.Context,
    private val http: HttpClient,
    private val settings: SettingsRepository,
    private val sourceDao: SourceDao,
    private val progressDao: ProgressDao,
    private val sourceImporter: SourceImporter,
    private val accountManager: CloudAccountManager,
    private val movieDao: MovieDao? = null,
    private val seriesDao: SeriesDao? = null,
    private val epgMigration: tv.own.owntv.core.epg.EpgMigration? = null,
) {
    private val prefs = context.getSharedPreferences("hantv_cloud_sync_urls", android.content.Context.MODE_PRIVATE)

    private fun getSyncedUrls(): Set<String> {
        return prefs.getStringSet("synced_urls", emptySet()) ?: emptySet()
    }

    private fun saveSyncedUrl(url: String) {
        if (url.isBlank()) return
        val current = getSyncedUrls().toMutableSet()
        current.add(url)
        prefs.edit().putStringSet("synced_urls", current).apply()
    }

    private fun removeSyncedUrl(url: String) {
        if (url.isBlank()) return
        val current = getSyncedUrls().toMutableSet()
        current.remove(url)
        prefs.edit().putStringSet("synced_urls", current).apply()
    }

    /**
     * Perform full bidirectional synchronization of IPTV sources and playback progress.
     */
    suspend fun syncAll(): Boolean = withContext(Dispatchers.IO) {
        val uid = settings.cloudUserId.first().takeIf { it.isNotBlank() } ?: return@withContext false
        val token = accountManager.getValidToken().takeIf { it.isNotBlank() } ?: return@withContext false

        val user = accountManager.currentUser.value ?: accountManager.restoreSession()
        val email = settings.cloudUserEmail.first().ifBlank { user?.email ?: "" }
        val isAdmin = email.trim().lowercase() in listOf("admin@hantv.com", "kilicemre3437@gmail.com")
        val isPremium = user?.isPremium == true || isAdmin || user?.plan?.lowercase() == "premium"

        try {
            if (isPremium) {
                syncSources(uid, token)
                syncProgress(uid, token)
            } else {
                Log.i(TAG, "Cloud sync disabled for free tier user ($uid) - local only")
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Cloud sync error for uid=$uid", e)
            false
        }
    }

    suspend fun deleteSourceFromCloud(source: tv.own.owntv.core.database.entity.SourceEntity) = withContext(Dispatchers.IO) {
        removeSyncedUrl(source.url)
        val uid = settings.cloudUserId.first().takeIf { it.isNotBlank() } ?: return@withContext
        val token = accountManager.getValidToken().takeIf { it.isNotBlank() } ?: return@withContext

        try {
            val directDocUrl = "https://firestore.googleapis.com/v1/projects/$FIREBASE_PROJECT_ID/databases/(default)/documents/users/$uid/sources/src_${source.id}?key=$FIREBASE_API_KEY"
            http.delete(directDocUrl, mapOf("Authorization" to "Bearer $token"))

            val url = "https://firestore.googleapis.com/v1/projects/$FIREBASE_PROJECT_ID/databases/(default)/documents/users/$uid/sources?key=$FIREBASE_API_KEY"
            val respStr = runCatching { http.getText(url, headers = mapOf("Authorization" to "Bearer $token")) }.getOrNull()
            if (respStr != null && respStr.contains("\"documents\"")) {
                val root = JSONObject(respStr)
                if (root.has("documents")) {
                    val docs = root.getJSONArray("documents")
                    for (i in 0 until docs.length()) {
                        val docObj = docs.getJSONObject(i)
                        val docName = docObj.optString("name", "")
                        val fields = docObj.optJSONObject("fields") ?: continue
                        val srcUrl = fields.optJSONObject("url")?.optString("stringValue", "") ?: ""
                        val username = fields.optJSONObject("username")?.optString("stringValue", "") ?: ""
                        
                        if (srcUrl == source.url || (username.isNotBlank() && username == source.username && srcUrl == source.url)) {
                            val subPath = docName.substringAfter("documents/")
                            val delUrl = "https://firestore.googleapis.com/v1/projects/$FIREBASE_PROJECT_ID/databases/(default)/documents/$subPath?key=$FIREBASE_API_KEY"
                            http.delete(delUrl, mapOf("Authorization" to "Bearer $token"))
                            Log.i(TAG, "Deleted cloud source document: $subPath")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting source from cloud: ${source.name}", e)
        }
    }

    private fun cleanUrl(url: String): String = url.trim().lowercase().removeSuffix("/")

    private suspend fun syncSources(uid: String, token: String) = withContext(Dispatchers.IO) {
        val url = "https://firestore.googleapis.com/v1/projects/$FIREBASE_PROJECT_ID/databases/(default)/documents/users/$uid/sources?key=$FIREBASE_API_KEY"
        val respStr = runCatching { http.getText(url, headers = mapOf("Authorization" to "Bearer $token")) }.getOrNull()
        
        val localSources = sourceDao.getAllOnce()
        val cloudSourceUrls = mutableSetOf<String>()
        val previouslySynced = getSyncedUrls()
        var fetchSuccess = false

        if (respStr != null) {
            fetchSuccess = true
            if (respStr.contains("\"documents\"")) {
                val root = JSONObject(respStr)
                if (root.has("documents")) {
                    val docs = root.getJSONArray("documents")
                    for (i in 0 until docs.length()) {
                        val docObj = docs.getJSONObject(i)
                        val fields = docObj.optJSONObject("fields") ?: continue

                        val name = fields.optJSONObject("name")?.optString("stringValue", "") ?: ""
                        val typeStr = fields.optJSONObject("type")?.optString("stringValue", "XTREAM") ?: "XTREAM"
                        val sourceUrl = fields.optJSONObject("url")?.optString("stringValue", "") ?: ""
                        val username = fields.optJSONObject("username")?.optString("stringValue", "") ?: ""
                        val password = fields.optJSONObject("password")?.optString("stringValue", "") ?: ""
                        val epgUrl = fields.optJSONObject("epgUrl")?.optString("stringValue", "") ?: ""
                        val userAgent = fields.optJSONObject("userAgent")?.optString("stringValue", "") ?: ""
                        val mac = fields.optJSONObject("mac")?.optString("stringValue", "") ?: ""

                        if (sourceUrl.isBlank()) continue
                        cloudSourceUrls.add(sourceUrl)
                        saveSyncedUrl(sourceUrl)

                        val type = runCatching { SourceType.valueOf(typeStr) }.getOrDefault(SourceType.XTREAM)
                        val cleanSrcUrl = cleanUrl(sourceUrl)
                        val exists = localSources.any { cleanUrl(it.url) == cleanSrcUrl || (it.username == username && cleanUrl(it.url) == cleanSrcUrl) }

                        if (!exists) {
                            Log.i(TAG, "Importing cloud source: $name ($typeStr)")
                            runCatching {
                                val activePid = settings.activeProfileId.first()
                                if (activePid > 0) {
                                    sourceImporter.useProfile(activePid)
                                }
                                when (type) {
                                    SourceType.XTREAM -> sourceImporter.xtream(name = name, server = sourceUrl, username = username, password = password, userAgent = userAgent, epgUrl = epgUrl)
                                    SourceType.M3U -> sourceImporter.m3u(name = name, url = sourceUrl, userAgent = userAgent, epgUrl = epgUrl)
                                    SourceType.STALKER -> sourceImporter.stalker(name = name, portalUrl = sourceUrl, mac = username.ifBlank { mac }, userAgent = userAgent)
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }
        }

        if (fetchSuccess) {
            val cloudCleanSet = cloudSourceUrls.map { cleanUrl(it) }.toSet()
            val prevCleanSet = previouslySynced.map { cleanUrl(it) }.toSet()

            // Delete local sources that were previously synced but are now missing from Cloud (remotely deleted)
            for (local in localSources) {
                if (local.url.isNotBlank()) {
                    val localClean = cleanUrl(local.url)
                    if (!cloudCleanSet.contains(localClean) && prevCleanSet.contains(localClean)) {
                        Log.i(TAG, "Deleting local source missing from Cloud: ${local.name}")
                        runCatching { sourceDao.delete(local) }
                        removeSyncedUrl(local.url)
                    }
                }
            }

            // Push fresh local sources to Cloud (newly added locally)
            val updatedLocal = sourceDao.getAllOnce()
            for (local in updatedLocal) {
                if (local.url.isNotBlank()) {
                    val localClean = cleanUrl(local.url)
                    if (!cloudCleanSet.contains(localClean) && !prevCleanSet.contains(localClean)) {
                        Log.i(TAG, "Pushing new local source to Cloud: ${local.name}")
                        val docId = "src_${local.id}"
                        val docUrl = "https://firestore.googleapis.com/v1/projects/$FIREBASE_PROJECT_ID/databases/(default)/documents/users/$uid/sources/$docId?key=$FIREBASE_API_KEY"
                        
                        val fieldsObj = JSONObject().apply {
                            put("name", JSONObject().put("stringValue", local.name))
                            put("type", JSONObject().put("stringValue", local.type.name))
                            put("url", JSONObject().put("stringValue", local.url))
                            put("username", JSONObject().put("stringValue", local.username ?: ""))
                            put("password", JSONObject().put("stringValue", local.password ?: ""))
                            put("epgUrl", JSONObject().put("stringValue", local.epgUrl ?: ""))
                            put("userAgent", JSONObject().put("stringValue", local.userAgent ?: ""))
                            put("mac", JSONObject().put("stringValue", local.mac ?: ""))
                            put("updatedAt", JSONObject().put("stringValue", System.currentTimeMillis().toString()))
                        }
                        val body = JSONObject().put("fields", fieldsObj).toString()
                        val res = runCatching { http.patchJson(docUrl, body, mapOf("Authorization" to "Bearer $token")) }
                        if (res.isSuccess) {
                            saveSyncedUrl(local.url)
                        }
                    }
                }
            }

            runCatching { epgMigration?.ensureEpgSourcesForPlaylists() }
        }
    }

    private suspend fun syncProgress(uid: String, token: String) = withContext(Dispatchers.IO) {
        val url = "https://firestore.googleapis.com/v1/projects/$FIREBASE_PROJECT_ID/databases/(default)/documents/users/$uid/progress?key=$FIREBASE_API_KEY"
        val respStr = runCatching { http.getText(url, headers = mapOf("Authorization" to "Bearer $token")) }.getOrNull()

        val localProgressList = progressDao.getAllOnce()

        if (respStr != null && respStr.contains("\"documents\"")) {
            val root = JSONObject(respStr)
            if (root.has("documents")) {
                val docs = root.getJSONArray("documents")
                for (i in 0 until docs.length()) {
                    val docObj = docs.getJSONObject(i)
                    val fields = docObj.optJSONObject("fields") ?: continue

                    val mediaTypeStr = fields.optJSONObject("mediaType")?.optString("stringValue", "MOVIE") ?: "MOVIE"
                    val itemId = fields.optJSONObject("targetId")?.optLong("integerValue", 0L)
                        ?: fields.optJSONObject("itemId")?.optLong("integerValue", 0L)
                        ?: 0L
                    val positionMs = fields.optJSONObject("positionMs")?.optLong("integerValue", 0L) ?: 0L
                    val durationMs = fields.optJSONObject("durationMs")?.optLong("integerValue", 0L) ?: 0L
                    val updatedAt = fields.optJSONObject("updatedAt")?.optLong("integerValue", 0L) ?: 0L

                    if (itemId <= 0L) continue
                    val mediaType = runCatching { MediaType.valueOf(mediaTypeStr) }.getOrDefault(MediaType.MOVIE)

                    val localMatch = localProgressList.find { it.mediaType == mediaType && it.itemId == itemId }
                    if (localMatch == null || updatedAt > localMatch.updatedAt) {
                        progressDao.save(
                            PlaybackProgressEntity(
                                profileId = localMatch?.profileId ?: 1L,
                                mediaType = mediaType,
                                itemId = itemId,
                                positionMs = positionMs,
                                durationMs = durationMs,
                                updatedAt = if (updatedAt > 0) updatedAt else System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
        }

        for (local in localProgressList) {
            val docId = "${local.mediaType.name.lowercase()}_${local.itemId}"
            val docUrl = "https://firestore.googleapis.com/v1/projects/$FIREBASE_PROJECT_ID/databases/(default)/documents/users/$uid/progress/$docId?key=$FIREBASE_API_KEY"

            val fieldsObj = JSONObject().apply {
                put("mediaType", JSONObject().put("stringValue", local.mediaType.name))
                put("targetId", JSONObject().put("integerValue", local.itemId))
                put("itemId", JSONObject().put("integerValue", local.itemId))
                put("positionMs", JSONObject().put("integerValue", local.positionMs))
                put("durationMs", JSONObject().put("integerValue", local.durationMs))
                put("updatedAt", JSONObject().put("integerValue", local.updatedAt))
            }
            val body = JSONObject().put("fields", fieldsObj).toString()
            runCatching { http.patchJson(docUrl, body, mapOf("Authorization" to "Bearer $token")) }
        }
    }
}
