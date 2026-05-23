package com.alaa.presentation.screen.dhikr

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alaa.presentation.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun DhikrScreen(vm: DhikrViewModel = koinViewModel()) {
    val state   by vm.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) { vm.syncServiceState(context) }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF022C22), Color(0xFF010F0A)))),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 8.dp)
            ) {
                Text("📿 الأذكار الصوتية", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Gold)
                Text("Background Dhikr", fontSize = 10.sp, color = TextSecondary, letterSpacing = 2.sp)
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
                colors   = CardDefaults.cardColors(containerColor = if (state.isRunning) Color(0x33065F46) else CardDark),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).background(if (state.isRunning) GreenLight else Color.Gray, CircleShape))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (state.isRunning) "يعمل الآن" else "متوقف",
                                fontSize = 15.sp,
                                color = if (state.isRunning) GreenLight else Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (state.isRunning)
                            Text("${state.selectedDhikr.textAr} • كل ${state.intervalMin} دقائق",
                                fontSize = 12.sp, color = TextSecondary)
                    }
                    Button(
                        onClick = { if (state.isRunning) vm.stop(context) else vm.start(context) },
                        colors  = ButtonDefaults.buttonColors(
                            containerColor = if (state.isRunning) Color(0xFF7F1D1D) else GreenMid
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (state.isRunning) "⏹ إيقاف" else "▶ تشغيل", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }

        item {
            Text("اختر الذِّكر", fontSize = 14.sp, color = Gold,
                modifier = Modifier.padding(start = 18.dp, top = 12.dp, bottom = 4.dp))
        }

        items(allDhikrs) { item ->
            val selected = item.id == state.selectedDhikr.id
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selected) GoldDim else CardDark)
                    .border(1.dp, if (selected) Gold else Color(0x33C9A84C), RoundedCornerShape(12.dp))
                    .clickable { vm.selectDhikr(item) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(item.textAr, fontSize = 15.sp,
                    color = if (selected) GoldLight else TextPrimary, fontWeight = FontWeight.Medium)
                if (selected) Text("✓", fontSize = 16.sp, color = Gold)
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
                colors   = CardDefaults.cardColors(containerColor = CardDark),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("تكرار ذكر واحد فقط", fontSize = 14.sp, color = TextPrimary)
                        Switch(checked = state.repeatSingle, onCheckedChange = { vm.setRepeatSingle(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Gold, checkedTrackColor = GoldDim))
                    }
                    HorizontalDivider(color = Color(0x22FFFFFF))
                    Text("الفترة بين الأذكار: ${state.intervalMin} دقيقة", fontSize = 14.sp, color = TextPrimary)
                    Slider(value = state.intervalMin.toFloat(), onValueChange = { vm.setInterval(it.toInt()) },
                        valueRange = 1f..60f, steps = 58,
                        colors = SliderDefaults.colors(thumbColor = Gold, activeTrackColor = GreenLight))
                    HorizontalDivider(color = Color(0x22FFFFFF))
                    Text("مستوى الصوت: ${(state.volume * 100).toInt()}%", fontSize = 14.sp, color = TextPrimary)
                    Slider(value = state.volume, onValueChange = { vm.setVolume(it, context) },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(thumbColor = Gold, activeTrackColor = GreenLight))
                }
            }
        }
    }
}
