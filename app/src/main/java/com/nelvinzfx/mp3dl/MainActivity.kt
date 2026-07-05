package com.nelvinzfx.mp3dl

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var sharedText by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleSharedIntent(intent)
        setContent {
            Mp3DlTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Mp3DlApp(sharedText = sharedText)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleSharedIntent(intent)
    }

    private fun handleSharedIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND) {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (text != null) {
                sharedText = text
            }
        }
    }
}

@Composable
private fun Mp3DlTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF1DB954),
            onPrimary = Color.Black,
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            onSurface = Color(0xFFEEEEEE),
            onBackground = Color(0xFFEEEEEE),
            surfaceVariant = Color(0xFF2A2A2A),
            onSurfaceVariant = Color(0xFFBBBBBB),
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Mp3DlApp(sharedText: String?) {
    var query by remember { mutableStateOf("") }
    val results = remember { mutableStateListOf<SearchResult>() }
    var searching by remember { mutableStateOf(false) }
    var downloadingId by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    var showAbout by remember { mutableStateOf(false) }
    var redownloadTarget by remember { mutableStateOf<SearchResult?>(null) }

    // persist downloaded IDs across app launches
    val downloadedIds = remember { mutableStateListOf<String>() }
    downloadedIds.addAll(DownloadTracker.getDownloaded(context))

    var pendingShared by remember { mutableStateOf<String?>(null) }
    if (sharedText != null) pendingShared = sharedText

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Storage permission needed to save downloads", Toast.LENGTH_SHORT).show()
        }
    }

    fun doSearch() {
        if (query.isBlank()) return
        keyboard?.hide()
        searching = true
        scope.launch {
            try {
                results.clear()
                val vid = Mp3Api.extractVideoId(query)
                if (vid != null) {
                    // YouTube link: fetch metadata directly
                    val meta = Mp3Api.fetchMetadata(vid)
                    if (meta != null) {
                        results.add(meta)
                        // async: fetch download URL + file size, update result
                        scope.launch {
                            val dlUrl = Mp3Api.fetchDownloadUrl(vid)
                            if (dlUrl.isNotEmpty()) {
                                val size = Mp3Api.fetchFileSize(dlUrl)
                                if (size.isNotEmpty()) {
                                    val idx = results.indexOfFirst { it.id == vid }
                                    if (idx >= 0) {
                                        results[idx] = results[idx].copy(size = size)
                                    }
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Could not fetch video info", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Normal keyword search
                    results.addAll(Mp3Api.search(query))
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Search failed", Toast.LENGTH_SHORT).show()
            }
            // sync downloaded state with actual file existence
            val toRemove = downloadedIds.filter { id ->
                val result = results.find { it.id == id }
                result != null && !DownloadTracker.isFileExists(result.title)
            }
            toRemove.forEach { id ->
                downloadedIds.remove(id)
                DownloadTracker.removeDownloaded(context, id)
            }
            searching = false
        }
    }

    // auto-search when launched via share intent
    LaunchedEffect(pendingShared) {
        val shared = pendingShared
        if (!shared.isNullOrBlank()) {
            query = shared
            pendingShared = null
            doSearch()
        }
    }

    fun performDownload(result: SearchResult) {
        downloadingId = result.id
        scope.launch {
            try {
                val url = Mp3Api.fetchDownloadUrl(result.id)
                if (url.isNotEmpty()) {
                    Mp3Downloader.download(context, url, result.title)
                    DownloadTracker.markDownloaded(context, result.id)
                    downloadedIds.add(result.id)
                    Toast.makeText(context, "Downloading: ${result.title}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to get download link", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
            }
            downloadingId = null
        }
    }

    fun doDownload(result: SearchResult) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                return
            }
        }
        // check if file already exists and user hasn't disabled warning
        if (DownloadTracker.isFileExists(result.title) && !DownloadTracker.shouldSkipRedownloadWarn(context)) {
            redownloadTarget = result
            return
        }
        performDownload(result)
    }

    if (showAbout) {
        AboutDialog(onDismiss = { showAbout = false })
    }

    redownloadTarget?.let { target ->
        ReDownloadDialog(
            title = target.title,
            onConfirm = { skipWarn ->
                DownloadTracker.setSkipRedownloadWarn(context, skipWarn)
                performDownload(target)
                redownloadTarget = null
            },
            onDismiss = { redownloadTarget = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MP3 Downloader", color = Color(0xFF1DB954)) },
                actions = {
                    IconButton(onClick = { showAbout = true }) {
                        Icon(Icons.Default.Info, contentDescription = "About", tint = Color(0xFF1DB954))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E1E1E)
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search songs or paste YouTube link...") },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSearch = { doSearch() }),
                trailingIcon = {
                    if (searching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        IconButton(onClick = { doSearch() }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1DB954),
                    unfocusedBorderColor = Color(0xFF444444),
                )
            )

            if (results.isEmpty() && !searching) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Search for songs to download", color = Color(0xFF888888))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(results, key = { it.id }) { result ->
                        ResultCard(
                            result = result,
                            isDownloading = downloadingId == result.id,
                            isDownloaded = downloadedIds.contains(result.id),
                            onDownload = { doDownload(result) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultCard(
    result: SearchResult,
    isDownloading: Boolean,
    isDownloaded: Boolean,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDownloaded) Color(0xFF1A2820) else Color(0xFF1E1E1E)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = result.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDownloaded) Color(0xFFAAAAAA) else Color(0xFFEEEEEE),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${result.duration} | ${result.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF888888)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            when {
                isDownloading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF1DB954)
                    )
                }
                isDownloaded -> {
                    IconButton(onClick = onDownload) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Downloaded - tap to re-download",
                            tint = Color(0xFF1DB954),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                else -> {
                    IconButton(onClick = onDownload) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Download",
                            tint = Color(0xFF1DB954)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("MP3 Downloader", color = Color(0xFF1DB954), fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    "A lightweight music downloader for Android.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFEEEEEE)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Built with", fontWeight = FontWeight.Bold, color = Color(0xFF1DB954))
                Text(
                    "Kotlin, Jetpack Compose, HttpURLConnection, Coil, Android DownloadManager",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFBBBBBB)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Purpose", fontWeight = FontWeight.Bold, color = Color(0xFF1DB954))
                Text(
                    "Search and download MP3 files with a clean, minimal dark interface. " +
                    "No ads, no bloat — just find a song and download it.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFBBBBBB)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Made by", fontWeight = FontWeight.Bold, color = Color(0xFF1DB954))
                Text(
                    "nelvinzfx",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFBBBBBB)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Version 1.3",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF888888)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF1DB954))
            }
        },
        containerColor = Color(0xFF1E1E1E),
        titleContentColor = Color(0xFF1DB954)
    )
}

@Composable
private fun ReDownloadDialog(
    title: String,
    onConfirm: (skipWarn: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var dontShowAgain by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("File already exists", color = Color(0xFF1DB954), fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    "\"$title\" is already in your Downloads folder.\nDo you want to download it again?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFEEEEEE)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = dontShowAgain,
                        onCheckedChange = { dontShowAgain = it },
                        colors = androidx.compose.material3.CheckboxDefaults.colors(
                            checkedColor = Color(0xFF1DB954),
                            uncheckedColor = Color(0xFF888888)
                        )
                    )
                    Text(
                        "Don't show this again",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFBBBBBB)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(dontShowAgain) }) {
                Text("Download again", color = Color(0xFF1DB954))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF888888))
            }
        },
        containerColor = Color(0xFF1E1E1E),
        titleContentColor = Color(0xFF1DB954)
    )
}
