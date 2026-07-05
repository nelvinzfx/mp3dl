package com.nelvinzfx.mp3dl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class SearchResult(
    val title: String,
    val id: String,
    val duration: String,
    val size: String,
    val thumbnailUrl: String
)

object Mp3Api {
    private const val SEARCH_URL = "https://mw.mp3juice.blog/search.php"
    private const val SCR_URL = "https://mapi.y2jar.cc/scr"
    private const val UA =
        "Mozilla/5.0 (Linux; Android 12; SM-N960N) " +
        "AppleWebKit/537.36 (KHTML, like Gecko) " +
        "Chrome/120.0.0.0 Mobile Safari/537.36"
    private const val MAX_RETRIES = 5
    private const val RETRY_DELAY = 2000L

    suspend fun search(query: String): List<SearchResult> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()

        val encoded = URLEncoder.encode(query, "UTF-8")
        val url = URL("$SEARCH_URL?q=$encoded")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("User-Agent", UA)
            setRequestProperty("Accept", "application/json")
            connectTimeout = 15000
            readTimeout = 15000
        }

        try {
            if (conn.responseCode == 200) {
                val body = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(body)
                val items = json.getJSONArray("items")
                (0 until items.length()).map { i ->
                    val item = items.getJSONObject(i)
                    val id = item.optString("id")
                    SearchResult(
                        title = item.optString("title"),
                        id = id,
                        duration = item.optString("duration"),
                        size = item.optString("size"),
                        thumbnailUrl = "https://i.ytimg.com/vi/$id/mqdefault.jpg"
                    )
                }
            } else {
                emptyList()
            }
        } finally {
            conn.disconnect()
        }
    }

    suspend fun fetchDownloadUrl(id: String): String = withContext(Dispatchers.IO) {
        if (id.isEmpty()) return@withContext ""

        val url = URL("$SCR_URL/$id?s=7")

        for (attempt in 0 until MAX_RETRIES) {
            try {
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", UA)
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("Referer", "https://m2.y2jar.cc/")
                    connectTimeout = 15000
                    readTimeout = 15000
                }

                try {
                    if (conn.responseCode == 200) {
                        val body = conn.inputStream.bufferedReader().readText()
                        val dl = JSONObject(body).optString("downloadUrl", "")
                        if (dl.isNotEmpty()) return@withContext dl
                    }
                } finally {
                    conn.disconnect()
                }
            } catch (_: Exception) {
            }

            if (attempt < MAX_RETRIES - 1) {
                Thread.sleep(RETRY_DELAY)
            }
        }
        ""
    }
}
