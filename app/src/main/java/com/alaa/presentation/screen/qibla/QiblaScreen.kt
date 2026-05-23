package com.alaa.presentation.screen.qibla

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.alaa.data.PrayerCalculator
import com.alaa.presentation.theme.*
import kotlin.math.*

@Composable
fun QiblaScreen() {
    val context = LocalContext.current
    val prefs   = context.getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE)

    var lat by remember { mutableStateOf(prefs.getFloat("lat", 30f).toDouble()) }
    var lng by remember { mutableStateOf(prefs.getFloat("lng", 31f).toDouble()) }
    var qibla by remember { mutableStateOf(PrayerCalculator.calcQibla(lat, lng)) }
    var heading by remember { mutableStateOf(0f) }
    var locationOk by remember { mutableStateOf(lat != 30.0 || lng != 31.0) }

    // Sensor listener for compass
    DisposableEffect(Unit) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rMat = FloatArray(9)
        val iMat = FloatArray(9)
        val orientation = FloatArray(3)
        var gravity = FloatArray(3)
        var magnetic = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                when (e.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> gravity = e.values.clone()
                    Sensor.TYPE_MAGNETIC_FIELD -> magnetic = e.values.clone()
                }
                if (gravity.isNotEmpty() && magnetic.isNotEmpty()) {
                    if (SensorManager.getRotationMatrix(rMat, iMat, gravity, magnetic)) {
                        SensorManager.getOrientation(rMat, orientation)
                        heading = (Math.toDegrees(orientation[0].toDouble()).toFloat() + 360f) % 360f
                    }
                }
            }
            override fun onAccuracyChanged(s: Sensor?, a: Int) {}
        }
        val acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val mag = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        acc?.let { sm.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME) }
        mag?.let { sm.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME) }
        onDispose { sm.unregisterListener(listener) }
    }

    // Animated rotation for smooth compass
    val animatedHeading by animateFloatAsState(
        targetValue = heading,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heading"
    )

    val locationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
        if (perms.values.any { it }) {
            try {
                @Suppress("MissingPermission")
                val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val last = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                last?.let {
                    lat = it.latitude; lng = it.longitude
                    qibla = PrayerCalculator.calcQibla(lat, lng)
                    locationOk = true
                    prefs.edit().putFloat("lat", lat.toFloat()).putFloat("lng", lng.toFloat()).apply()
                }
            } catch (_: Exception) {}
        }
    }

    LaunchedEffect(Unit) {
        val ok = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!ok) locationLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF022C22), Color(0xFF010F0A)))),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Text("🧭 اتجاه القبلة", fontSize = 20.sp, color = Gold, fontWeight = FontWeight.Bold)
        Text("Qibla Compass", fontSize = 11.sp, color = TextSecondary, letterSpacing = 2.sp)
        Spacer(Modifier.height(24.dp))

        // Compass Canvas
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f; val cy = size.height / 2f; val r = size.minDimension / 2f - 8f

                // Outer circle
                drawCircle(color = Color(0xFF064E3B), radius = r)
                drawCircle(color = Gold.copy(alpha = 0.4f), radius = r, style = androidx.compose.ui.graphics.drawscope.Stroke(2f))

                // Degree ticks
                for (i in 0 until 360 step 5) {
                    val isMaj = i % 30 == 0; val isMed = i % 10 == 0
                    val rad = Math.toRadians((i - animatedHeading).toDouble())
                    val inner = r * if (isMaj) 0.72f else if (isMed) 0.78f else 0.84f
                    val outer = r * 0.9f
                    val x1 = cx + sin(rad).toFloat() * inner; val y1 = cy - cos(rad).toFloat() * inner
                    val x2 = cx + sin(rad).toFloat() * outer; val y2 = cy - cos(rad).toFloat() * outer
                    drawLine(
                        color = if (isMaj) Gold.copy(0.9f) else if (isMed) Gold.copy(0.45f) else Color.White.copy(0.2f),
                        start = Offset(x1, y1), end = Offset(x2, y2),
                        strokeWidth = if (isMaj) 2f else if (isMed) 1.2f else 0.7f
                    )
                }

                // Qibla beam
                val qRad = Math.toRadians((qibla - animatedHeading).toDouble())
                drawLine(
                    brush = Brush.linearGradient(listOf(Color.Transparent, GreenLight.copy(0.7f)),
                        start = Offset(cx, cy), end = Offset((cx + sin(qRad).toFloat() * r * 0.8f), (cy - cos(qRad).toFloat() * r * 0.8f))),
                    start = Offset(cx, cy),
                    end   = Offset(cx + sin(qRad).toFloat() * r * 0.8f, cy - cos(qRad).toFloat() * r * 0.8f),
                    strokeWidth = 3f
                )

                // North needle
                val nRad = Math.toRadians(-animatedHeading.toDouble())
                // North (gold)
                drawLine(color = Gold, start = Offset(cx, cy),
                    end = Offset(cx + sin(nRad).toFloat() * r * 0.65f, cy - cos(nRad).toFloat() * r * 0.65f), strokeWidth = 4f)
                // South (gray)
                drawLine(color = Color.Gray.copy(0.6f), start = Offset(cx, cy),
                    end = Offset(cx - sin(nRad).toFloat() * r * 0.55f, cy + cos(nRad).toFloat() * r * 0.55f), strokeWidth = 3f)

                // Center jewel
                drawCircle(color = Gold, radius = 10f, center = Offset(cx, cy))
                drawCircle(color = Color.White.copy(0.5f), radius = 4f, center = Offset(cx, cy))
            }

            // Kaaba emoji in center
            Text("🕋", fontSize = 22.sp)
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Center) {
            Text("${qibla.toInt()}", fontSize = 48.sp, fontWeight = FontWeight.Black, color = GreenLight)
            Text("°", fontSize = 24.sp, color = Gold, fontWeight = FontWeight.Bold)
        }
        Text(if (locationOk) "القبلة تقع على ${qibla.toInt()}° من الشمال" else "جارٍ تحديد الموقع...", fontSize = 14.sp, color = TextSecondary)
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            colors = CardDefaults.cardColors(containerColor = CardDark),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(14.dp)) {
                Text("الكعبة المشرّفة — مكة المكرمة", fontSize = 14.sp, color = Gold, fontWeight = FontWeight.Bold)
                Text("21.4225°N · 39.8262°E", fontSize = 11.sp, color = TextSecondary, letterSpacing = 1.sp)
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("↑ اتجاه الشمال المغناطيسي (ذهبي) — اتجاه القبلة (أخضر)", fontSize = 11.sp, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 24.dp))
    }
}
