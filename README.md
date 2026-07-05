# mp3dl

A lightweight Android app to search and download MP3 files. Dark mode only, minimal UI, no ads, no bloat.

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

Grab the latest APK from the [Actions](../../actions) page, or build it yourself:

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

---

# mp3dl (Indonesia)

Aplikasi Android ringan buat cari dan download lagu MP3. Dark mode doang, tampilan minimalis, ga ada iklan, ga ada yang ribet.

## Fitur

- Cari lagu pake keyword
- Download MP3 langsung ke folder Downloads
- Thumbnail lagu di tiap hasil pencarian
- Lagu yang udah di-download ada tanda checkmark
- Dialog About berisi info aplikasi
- Dark mode doang, accent warna hijau
- APK cuma 1.4 MB

## Dibuat Pake

- Kotlin + Jetpack Compose
- HttpURLConnection (tanpa OkHttp)
- Coil buat load gambar
- Android DownloadManager
- GitHub Actions buat build CI

## Cara Kerjanya

1. Pencarian dikirim ke `mw.mp3juice.blog/search.php`
2. Link download diambil dari `mapi.y2jar.cc/scr/<id>?s=7`
3. Thumbnail diambil dari `i.ytimg.com/vi/<id>/mqdefault.jpg`
4. File disimpen ke `/sdcard/Download/` pake Android DownloadManager

## Install

Ambil APK terbaru dari halaman [Actions](../../actions), atau build sendiri:

```bash
git clone https://github.com/nelvinzfx/mp3dl
cd mp3dl
gradle assembleRelease
```

Butuh JDK 17 dan Gradle 8.9. Buat build release yang signed, siapin keystore sebagai `mp3dl.jks` dan set secret `KEY_BASE64` di repo kamu.

## Tampilan

- **Search bar** di atas, ketik nama lagu terus pencet search
- **List hasil** nampilin thumbnail, judul, durasi, ukuran file
- **Tombol download** di tiap card, berubah jadi spinner pas lagi ambil link
- **Checkmark** muncul abis lagu itu di-download
- **About** di pojok kanan atas

## Lisensi

Ga ada. Mau diapain aja bebas.
