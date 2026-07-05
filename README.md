<p align="center">
  <img src="assets/logo.svg" width="120" alt="mp3dl logo" />
</p>

<h1 align="center">mp3dl</h1>

<p align="center">
  <a href="https://github.com/nelvinzfx/mp3dl/releases"><img src="https://img.shields.io/github/v/release/nelvinzfx/mp3dl?style=plastic&color=1DB954&label=release&logo=github&logoColor=white" alt="Release"></a>
  <img src="https://img.shields.io/badge/APK-1.4_MB-1DB954?style=plastic&logo=android&logoColor=white" alt="APK Size">
  <img src="https://img.shields.io/badge/Android-API_26%2B-1DB954?style=plastic&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Kotlin-2.0.20-7F52FF?style=plastic&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Compose-BOM_2024.09-4285F4?style=plastic&logo=jetpackcompose&logoColor=white" alt="Compose">
  <img src="https://img.shields.io/badge/license-none-1DB954?style=plastic&logo=github&logoColor=white" alt="License">
</p>

A lightweight Android app to search and download MP3 files. Dark mode only, minimal UI, no ads, no bloat.

<p align="center">
  <a href="README_ID.md"><img src="https://img.shields.io/badge/Baca_dalam-Bahasa_Indonesia-1DB954?style=plastic&logo=googletranslate&logoColor=white" alt="Bahasa Indonesia"></a>
</p>

## Features

- Search songs by keyword or paste YouTube/YT Music link
- Share from YouTube/YT Music directly into the app
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
