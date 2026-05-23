package com.alaa.presentation.screen.tasbih

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alaa.presentation.theme.*

private val TASBIH_OPTIONS = listOf(
    "سُبحَانَ الله",
    "الحَمدُ لله",
    "الله أَكبَر",
    "لَا إِلَهَ إِلَّا الله",
    "أَستَغفِرُ الله",
    "لَا حَولَ وَلَا قُوَّةَ إِلَّا بِالله",
    "اللَّهُمَّ صَلِّ عَلَى مُحَمَّد",
    "سُبحَانَ الله وَبِحَمدِه",
    "سُبحَانَ الله العَظِيم"
)

private val TARGETS = listOf(33, 34, 100, 99, 1000)

@Composable
fun TasbihScreen() {
    var count        by remember { mutableStateOf(0) }
    var totalCount   by remember { mutableStateOf(0) }
    var target       by remember { mutableStateOf(33) }
    var selectedText by remember { mutableStateOf(TASBIH_OPTIONS[0]) }
    var laps         by remember { mutableStateOf(0) }
    val haptic = LocalHapticFeedback.current

    // Button press animation
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.93f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "press"
    )

    // Glow pulse
    val glowTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by glowTransition.animateFloat(
        initialValue = 0.25f, targetValue = 0.55f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow_alpha"
    )

    val progress = (count.toFloat() / target).coerceIn(0f, 1f)
    val completed = count >= target

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF022C22), Color(0xFF010F0A)))),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Text("📿 المسبحة", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Gold)
        Text("Digital Tasbih", fontSize = 11.sp, color = TextSecondary, letterSpacing = 2.sp)
        Spacer(Modifier.height(12.dp))

        // ── Tasbih selector ──────────────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(TASBIH_OPTIONS) { _, text ->
                val sel = text == selectedText
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (sel) Gold else Color(0x33064E3B))
                        .border(1.dp, if (sel) Gold else Color(0x44C9A84C), RoundedCornerShape(20.dp))
                        .clickable { selectedText = text; count = 0 }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(text, fontSize = 12.sp, color = if (sel) Color.Black else TextPrimary, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // ── Target selector ──────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("الهدف:", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.align(Alignment.CenterVertically))
            TARGETS.forEach { t ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (t == target) Gold else Color(0x33064E3B))
                        .border(1.dp, if (t == target) Gold else Color(0x44C9A84C), CircleShape)
                        .clickable { target = t; count = 0 }
                        .size(40.dp)
                ) { Text("$t", fontSize = 11.sp, color = if (t == target) Color.Black else TextPrimary, fontWeight = FontWeight.Bold) }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Progress ring (circular) ──────────────────────────────
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
            // Glow
            Box(
                Modifier.size(220.dp).background(
                    Gold.copy(alpha = glowAlpha * 0.08f), CircleShape
                )
            )
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(210.dp),
                strokeWidth = 10.dp,
                color = if (completed) GreenLight else Gold,
                trackColor = Color(0x22064E3B)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "$count",
                    fontSize = 60.sp, fontWeight = FontWeight.Black,
                    color = if (completed) GreenLight else Gold
                )
                Text(selectedText, fontSize = 14.sp, color = TextPrimary, textAlign = TextAlign.Center)
                if (laps > 0) Text("× $laps دورة", fontSize = 11.sp, color = TextSecondary)
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("الإجمالي: $totalCount تسبيحة", fontSize = 12.sp, color = TextSecondary)

        Spacer(Modifier.height(20.dp))

        // ── Main TAP button ──────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(170.dp)
                .scale(scale)
                .background(
                    Brush.radialGradient(
                        listOf(if (completed) GreenLight else Gold, if (completed) GreenMid else GreenMid)
                    ), CircleShape
                )
                .border(3.dp, if (completed) GreenLight.copy(0.4f) else Gold.copy(0.3f), CircleShape)
                .clickable {
                    pressed = true
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    count++; totalCount++
                    if (count > target) { count = 1; laps++ }
                    else if (count == target) { laps++ }
                    pressed = false
                }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (completed) "✓" else "📿", fontSize = 36.sp)
                Text("اضغط", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Reset ────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { count = 0; laps = 0 },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44C9A84C)),
                shape  = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("إعادة", fontSize = 13.sp)
            }
            OutlinedButton(
                onClick = { count = 0; totalCount = 0; laps = 0 },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44EF4444)),
                shape  = RoundedCornerShape(12.dp)
            ) {
                Text("مسح الكل", fontSize = 13.sp)
            }
        }
    }
}
