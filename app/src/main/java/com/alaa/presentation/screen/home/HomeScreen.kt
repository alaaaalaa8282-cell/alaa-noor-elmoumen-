package com.alaa.presentation.screen.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.alaa.presentation.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if (perms.values.any { it }) viewModel.fetchLocation(context)
    }

    LaunchedEffect(Unit) {
        val ok = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (ok) viewModel.fetchLocation(context)
        else locationLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF022C22), Color(0xFF010F0A)))
        ),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // ── Header ─────────────────────────────────────────────────
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 8.dp)
            ) {
                Text("🕌", fontSize = 52.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    "محمد عبد العظيم الطويل",
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Gold,
                    textAlign = TextAlign.Center
                )
                Text("Noor Islamic App", fontSize = 11.sp, color = TextSecondary, letterSpacing = 2.sp)
            }
        }

        // ── Hijri Date ─────────────────────────────────────────────
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
                colors   = CardDefaults.cardColors(containerColor = CardDark),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        "${state.hijriDay} ${state.hijriMonth} ${state.hijriYear} هـ",
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Gold
                    )
                    Text(state.gregorian, fontSize = 13.sp, color = TextSecondary)
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Box(
                            Modifier.size(8.dp).background(
                                if (state.locationOk) GreenLight else Color.Gray, CircleShape
                            )
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            if (state.locationOk) "تم تحديد الموقع ✓" else "جارٍ تحديد الموقع...",
                            fontSize = 12.sp, color = if (state.locationOk) GreenLight else Color.Gray
                        )
                    }
                }
            }
        }

        // ── Prayer Times Grid ──────────────────────────────────────
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
                colors   = CardDefaults.cardColors(containerColor = CardDark),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("🕌 مواقيت الصلاة", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Gold)
                    Text("Prayer Times", fontSize = 10.sp, color = TextSecondary, letterSpacing = 1.5.sp)
                    Spacer(Modifier.height(12.dp))

                    val salah = state.prayerTimes.filter {
                        it.nameAr in listOf("الفجر", "الشروق", "الظهر", "العصر", "المغرب", "العشاء")
                    }
                    salah.chunked(3).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { prayer ->
                                val isNext = prayer.nameAr == state.nextPrayer
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isNext) GoldDim else Color(0x22064E3B),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isNext) Gold else Color(0x33C9A84C),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(vertical = 10.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(prayer.nameAr, fontSize = 13.sp, color = Gold, fontWeight = FontWeight.Bold)
                                        Text(prayer.nameEn, fontSize = 9.sp, color = TextSecondary)
                                        Spacer(Modifier.height(4.dp))
                                        Text(prayer.time, fontSize = 15.sp, color = if (isNext) GoldLight else TextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            // fill empty cells
                            repeat(3 - row.size) { Box(Modifier.weight(1f)) }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }

        // ── Countdown ──────────────────────────────────────────────
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
                colors   = CardDefaults.cardColors(containerColor = CardDark),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("الوقت المتبقي لصلاة", fontSize = 13.sp, color = TextSecondary)
                    Text(state.nextPrayer, fontSize = 17.sp, color = Gold, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        state.countdown,
                        fontSize = 38.sp, fontWeight = FontWeight.Black, color = GreenLight,
                        letterSpacing = 4.sp
                    )
                }
            }
        }

        // ── Motivation ─────────────────────────────────────────────
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
                colors   = CardDefaults.cardColors(containerColor = CardDark),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("✨ آية اليوم", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "﴿ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ ﴾",
                        fontSize = 17.sp, color = TextPrimary,
                        fontWeight = FontWeight.Medium, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Verily, in the remembrance of Allah do hearts find rest.",
                        fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("سورة الرعد (13:28)", fontSize = 11.sp, color = Gold,
                        textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}
