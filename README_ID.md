# mp3dl

Aplikasi Android ringan buat cari dan download lagu MP3. Dark mode doang, tampilan minimalis, ga ada iklan, ga ada yang ribet.

[Read in English](README.md)

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

Ambil APK terbaru dari halaman [Releases](../../releases), atau build sendiri:

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
