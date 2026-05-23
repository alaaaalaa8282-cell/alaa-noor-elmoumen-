package com.alaa.presentation.screen.azkar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alaa.presentation.theme.*

data class Zekr(val text: String, val count: Int, val translation: String = "")

val morningAzkar = listOf(
    Zekr("أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ", 1, "We reached the morning and the dominion belongs to Allah"),
    Zekr("اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ وَإِلَيْكَ النُّشُورُ", 1),
    Zekr("سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", 100, "Glory be to Allah and praise Him"),
    Zekr("لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", 10),
    Zekr("اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَٰهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ", 1),
    Zekr("رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا", 3)
)

val eveningAzkar = listOf(
    Zekr("أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ", 1),
    Zekr("اللَّهُمَّ عَافِنِي فِي بَدَنِي، اللَّهُمَّ عَافِنِي فِي سَمْعِي وَبَصَرِي", 3),
    Zekr("بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ", 3),
    Zekr("أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ", 3),
    Zekr("حَسْبِيَ اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ، عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ", 7)
)

val afterPrayerAzkar = listOf(
    Zekr("أَسْتَغْفِرُ اللَّهَ", 3, "I seek Allah's forgiveness"),
    Zekr("اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ", 1),
    Zekr("سُبْحَانَ اللَّهِ", 33, "Glory be to Allah"),
    Zekr("الْحَمْدُ لِلَّهِ", 33, "Praise be to Allah"),
    Zekr("اللَّهُ أَكْبَرُ", 33, "Allah is the Greatest"),
    Zekr("لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ", 10)
)

@Composable
fun AzkarScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("أذكار الصباح", "أذكار المساء", "أذكار الصلاة")
    val currentList = when (selectedTab) { 0 -> morningAzkar; 1 -> eveningAzkar; else -> afterPrayerAzkar }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF022C22), Color(0xFF010F0A))))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 8.dp)) {
            Text("📖 الأذكار", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Gold)
            Text("Daily Remembrance", fontSize = 10.sp, color = TextSecondary, letterSpacing = 2.sp)
        }
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            tabs.forEachIndexed { i, tab ->
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == i) GoldDim else CardDark)
                    .border(1.dp, if (selectedTab == i) Gold else Color(0x22C9A84C), RoundedCornerShape(10.dp))
                    .clickable { selectedTab = i }.padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center) {
                    Text(tab, fontSize = 12.sp, color = if (selectedTab == i) GoldLight else TextSecondary,
                        fontWeight = if (selectedTab == i) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center)
                }
            }
        }
        LazyColumn(contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(currentList) { zekr -> ZekrCard(zekr) }
        }
    }
}

@Composable
fun ZekrCard(zekr: Zekr) {
    var remaining by remember(zekr.text) { mutableStateOf(zekr.count) }
    val done = remaining == 0
    Card(colors = CardDefaults.cardColors(containerColor = if (done) Color(0x33065F46) else CardDark),
        shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(zekr.text, fontSize = 16.sp, color = if (done) TextSecondary else TextPrimary,
                fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            if (zekr.translation.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(zekr.translation, fontSize = 11.sp, color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier.size(44.dp)
                    .background(if (done) Color(0x22065F46) else GoldDim, CircleShape)
                    .border(2.dp, if (done) GreenLight else Gold, CircleShape),
                    contentAlignment = Alignment.Center) {
                    Text(if (done) "✓" else "$remaining", fontSize = 15.sp,
                        color = if (done) GreenLight else Gold, fontWeight = FontWeight.Black)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!done) Button(onClick = { if (remaining > 0) remaining-- },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenMid),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)) {
                        Text("تسبيح", color = Color.White, fontSize = 13.sp)
                    }
                    OutlinedButton(onClick = { remaining = zekr.count },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)) {
                        Text("إعادة", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
            if (zekr.count > 1) {
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 1f - remaining.toFloat() / zekr.count },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = if (done) GreenLight else Gold, trackColor = Color(0x22FFFFFF))
            }
        }
    }
}
