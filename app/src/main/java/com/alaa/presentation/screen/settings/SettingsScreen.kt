package com.alaa.presentation.screen.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alaa.presentation.theme.*

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val prefs   = context.getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE)
    val methods = listOf("Makkah", "Egypt", "MWL", "ISNA", "Kuwait", "Gulf", "Qatar", "Singapore", "Turkey")
    var calcMethod by remember { mutableStateOf(prefs.getString("calcMethod", "Makkah") ?: "Makkah") }
    var methodExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF022C22), Color(0xFF010F0A))))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 10.dp)
        ) {
            Text("⚙️ الإعدادات", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Gold)
            Text("Settings", fontSize = 11.sp, color = TextSecondary, letterSpacing = 2.sp)
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // ── Calculation method ────────────────────────────────
            item {
                SettingsCard(title = "طريقة حساب مواقيت الصلاة", icon = Icons.Default.Calculate) {
                    ExposedDropdownMenuBox(
                        expanded = methodExpanded,
                        onExpandedChange = { methodExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = calcMethod,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("الطريقة", color = TextSecondary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = methodExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = Gold,
                                unfocusedBorderColor = Color(0x44C9A84C),
                                focusedTextColor     = TextPrimary,
                                unfocusedTextColor   = TextPrimary
                            ),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = methodExpanded,
                            onDismissRequest = { methodExpanded = false },
                            modifier = Modifier.background(SurfaceDark)
                        ) {
                            methods.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m, color = if (m == calcMethod) Gold else TextPrimary) },
                                    onClick = {
                                        calcMethod = m
                                        prefs.edit().putString("calcMethod", m).apply()
                                        methodExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ── Battery optimization ──────────────────────────────
            item {
                SettingsCard(title = "إعدادات البطارية", icon = Icons.Default.BatteryFull) {
                    Text(
                        "لضمان عمل الأذان والأذكار في الخلفية، يُرجى تعطيل تحسين البطارية للتطبيق",
                        fontSize = 13.sp, color = TextSecondary
                    )
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                            if (!pm.isIgnoringBatteryOptimizations(context.packageName)) {
                                context.startActivity(
                                    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                        data = Uri.parse("package:${context.packageName}")
                                    }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Gold),
                        shape  = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("تعطيل تحسين البطارية", color = Color.Black, fontWeight = FontWeight.Bold) }
                }
            }

            // ── About ─────────────────────────────────────────────
            item {
                SettingsCard(title = "عن التطبيق", icon = Icons.Default.Info) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("🕌", fontSize = 36.sp)
                        Spacer(Modifier.height(6.dp))
                        Text("نور — تطبيق إسلامي", fontSize = 16.sp, color = Gold, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("إهداءً لروح", fontSize = 13.sp, color = TextSecondary)
                        Text("محمد عبد العظيم الطويل", fontSize = 15.sp, color = Gold, fontWeight = FontWeight.Bold)
                        Text("رحمه الله وأسكنه فسيح جناته", fontSize = 12.sp, color = TextSecondary)
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0x22C9A84C))
                        Spacer(Modifier.height(8.dp))
                        Text("الإصدار 1.0.0", fontSize = 12.sp, color = TextSecondary)
                        Text("com.alaa", fontSize = 11.sp, color = Color(0x66FFFFFF))
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun SettingsCard(
    title  : String,
    icon   : ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape  = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Gold, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontSize = 14.sp, color = Gold, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}
