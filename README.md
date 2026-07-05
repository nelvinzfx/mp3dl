# mp3dl

[![Release](https://img.shields.io/github/v/release/nelvinzfx/mp3dl?style=flat&color=1DB954&label=release)](https://github.com/nelvinzfx/mp3dl/releases)
[![APK Size](https://img.shields.io/badge/APK-1.4_MB-1DB954?style=flat)](https://github.com/nelvinzfx/mp3dl/releases)
[![Android](https://img.shields.io/badge/Android-API_26%2B-1DB954?style=flat)](https://github.com/nelvinzfx/mp3dl/releases)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.20-7F52FF?style=flat)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-BOM_2024.09-4285F4?style=flat)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/license-none-1DB954?style=flat)](https://github.com/nelvinzfx/mp3dl)

A lightweight Android app to search and download MP3 files. Dark mode only, minimal UI, no ads, no bloat.

[Baca dalam Bahasa Indonesia](README_ID.md)

## Features

- Search songs by keyword
- Download MP3 directly to your Downloads folder
- Album thumbnail on each result
- Downloaded songs get a checkmark indicator
- About dialog with app info
- Dark mode only, green accent
- 1.4 MB APK

## Built With

- Kotlin + Jetpack Compose
- HttpURLConnection (no OkHttp)
- Coil for image loading
- Android DownloadManager
- GitHub Actions for CI builds

## How It Works

1. Search queries go to `mw.mp3juice.blog/search.php`
2. Download links fetched from `mapi.y2jar.cc/scr/<id>?s=7`
3. Thumbnails pulled from `i.ytimg.com/vi/<id>/mqdefault.jpg`
4. Files saved to `/sdcard/Download/` via Android DownloadManager

## Install

Grab the latest APK from the [Releases](../../releases) page, or build it yourself:

```bash
git clone https://github.com/nelvinzfx/mp3dl
cd mp3dl
gradle assembleRelease
```

You need JDK 17 and Gradle 8.9. For signed release builds, provide a keystore as `mp3dl.jks` and set the `KEY_BASE64` secret in your repo.

## Screens

- **Search bar** at top, type a song name and hit search
- **Result list** shows thumbnail, title, duration, file size
- **Download button** on each card, turns into a spinner while fetching
- **Checkmark** appears after a song is downloaded
- **About** icon in the top right corner

## License

None. Do whatever you want with it.
