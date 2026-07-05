package com.nelvinzfx.mp3dl

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

object Mp3Downloader {
    fun download(context: Context, url: String, title: String) {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle(title)
            setDescription("MP3 Downloader")
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "${sanitize(title)}.mp3"
            )
            setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }
        dm.enqueue(request)
    }

    private fun sanitize(name: String): String {
        return name.replace(Regex("[\\\\/:*?\"<>|]"), "_").take(80)
    }
}
