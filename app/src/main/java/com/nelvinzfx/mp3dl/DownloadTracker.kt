package com.nelvinzfx.mp3dl

import android.content.Context
import android.content.SharedPreferences

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
}
