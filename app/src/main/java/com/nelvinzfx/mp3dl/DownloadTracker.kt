package com.nelvinzfx.mp3dl

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import org.json.JSONObject
import java.io.File

object DownloadTracker {
    private const val PREFS = "mp3dl_downloads"
    private const val KEY_HISTORY = "download_history"
    private const val KEY_SKIP_REDOWNLOAD_WARN = "skip_redownload_warn"
    private const val KEY_SKIP_DELETE_WARN = "skip_delete_warn"

    data class DownloadEntry(val id: String, val title: String)

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun getHistory(ctx: Context): Map<String, String> {
        val json = prefs(ctx).getString(KEY_HISTORY, "") ?: ""
        if (json.isEmpty()) return emptyMap()
        return try {
            val obj = JSONObject(json)
            obj.keys().asSequence().associateWith { obj.optString(it) }
        } catch (_: Exception) { emptyMap() }
    }

    fun getDownloadedIds(ctx: Context): Set<String> = getHistory(ctx).keys

    fun getDownloaded(ctx: Context): Set<String> = getDownloadedIds(ctx)

    fun markDownloaded(ctx: Context, id: String, title: String) {
        val history = getHistory(ctx).toMutableMap()
        history[id] = title
        prefs(ctx).edit().putString(KEY_HISTORY, JSONObject(history).toString()).apply()
    }

    fun removeDownloaded(ctx: Context, id: String) {
        val history = getHistory(ctx).toMutableMap()
        history.remove(id)
        prefs(ctx).edit().putString(KEY_HISTORY, JSONObject(history).toString()).apply()
    }

    fun deleteFile(title: String): Boolean {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(dir, "${sanitize(title)}.mp3")
        return file.exists() && file.delete()
    }

    fun isFileExists(title: String): Boolean {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(dir, "${sanitize(title)}.mp3")
        return file.exists()
    }

    fun getFileInfo(title: String): File? {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(dir, "${sanitize(title)}.mp3")
        return if (file.exists()) file else null
    }

    fun shouldSkipRedownloadWarn(ctx: Context): Boolean =
        prefs(ctx).getBoolean(KEY_SKIP_REDOWNLOAD_WARN, false)

    fun setSkipRedownloadWarn(ctx: Context, skip: Boolean) {
        prefs(ctx).edit().putBoolean(KEY_SKIP_REDOWNLOAD_WARN, skip).apply()
    }

    fun shouldSkipDeleteWarn(ctx: Context): Boolean =
        prefs(ctx).getBoolean(KEY_SKIP_DELETE_WARN, false)

    fun setSkipDeleteWarn(ctx: Context, skip: Boolean) {
        prefs(ctx).edit().putBoolean(KEY_SKIP_DELETE_WARN, skip).apply()
    }

    private fun sanitize(name: String): String {
        return name.replace(Regex("[\\\\/:*?\"<>|]"), "_").take(80)
    }
}
