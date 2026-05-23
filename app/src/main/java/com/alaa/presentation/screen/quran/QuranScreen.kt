package com.alaa.presentation.screen.quran

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.alaa.presentation.theme.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun QuranScreen() {
    var loading by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF022C22), Color(0xFF010F0A))))
    ) {
        // ── Header ────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 10.dp)
        ) {
            Text("📖 القرآن الكريم", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Gold)
            Text("Holy Quran — Audio", fontSize = 11.sp, color = TextSecondary, letterSpacing = 2.sp)
        }

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Gold)
                    Spacer(Modifier.height(12.dp))
                    Text("جارٍ التحميل...", fontSize = 14.sp, color = TextSecondary)
                }
            }
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.apply {
                        javaScriptEnabled       = true
                        domStorageEnabled        = true
                        mediaPlaybackRequiresUserGesture = false
                        cacheMode               = WebSettings.LOAD_DEFAULT
                        mixedContentMode        = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            loading = false
                        }
                    }
                    webChromeClient = WebChromeClient()
                    loadUrl("https://qari.app")
                }
            }
        )
    }
}
