package com.nelvinzfx.mp3dl

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import java.io.File

object DownloadTracker {
    private const val PREFS = "mp3dl_downloads"
    private const val KEY_IDS = "downloaded_ids"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun getDownloaded(ctx: Context): Set<String> =
        prefs(ctx).getStringSet(KEY_IDS, emptySet()) ?: emptySet()

    fun markDownloaded(ctx: Context, id: String) {
        val current = getDownloaded(ctx).toMutableSet()
        current.add(id)
        prefs(ctx).edit().putStringSet(KEY_IDS, current).apply()
    }

    fun removeDownloaded(ctx: Context, id: String) {
        val current = getDownloaded(ctx).toMutableSet()
        current.remove(id)
        prefs(ctx).edit().putStringSet(KEY_IDS, current).apply()
    }

    fun isFileExists(title: String): Boolean {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(dir, "${sanitize(title)}.mp3")
        return file.exists()
    }

    private fun sanitize(name: String): String {
        return name.replace(Regex("[\\\\/:*?\"<>|]"), "_").take(80)
    }

    private const val KEY_SKIP_REDOWNLOAD_WARN = "skip_redownload_warn"

    fun shouldSkipRedownloadWarn(ctx: Context): Boolean =
        prefs(ctx).getBoolean(KEY_SKIP_REDOWNLOAD_WARN, false)

    fun setSkipRedownloadWarn(ctx: Context, skip: Boolean) {
        prefs(ctx).edit().putBoolean(KEY_SKIP_REDOWNLOAD_WARN, skip).apply()
    }
}
