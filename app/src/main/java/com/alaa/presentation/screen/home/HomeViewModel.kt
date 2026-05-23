package com.alaa.presentation.screen.home

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alaa.data.PrayerCalculator
import com.alaa.data.models.PrayerTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val prayerTimes : List<PrayerTime>  = emptyList(),
    val hijriDay    : Int               = 0,
    val hijriMonth  : String            = "",
    val hijriYear   : Int               = 0,
    val gregorian   : String            = "",
    val countdown   : String            = "--:--:--",
    val nextPrayer  : String            = "",
    val locationOk  : Boolean           = false,
    val lat         : Double            = 30.0,
    val lng         : Double            = 31.0,
    val calcMethod  : String            = "Makkah",
    val darkMode    : Boolean           = true
)

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE)
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private var countdownJob: Job? = null
    private val SALAH = listOf("الفجر", "الظهر", "العصر", "المغرب", "العشاء")

    init {
        loadSavedState()
        updateHijri()
        recalcPrayers()
        startCountdown()
    }

    private fun loadSavedState() {
        _state.value = _state.value.copy(
            lat        = prefs.getFloat("lat", 30f).toDouble(),
            lng        = prefs.getFloat("lng", 31f).toDouble(),
            calcMethod = prefs.getString("calcMethod", "Makkah") ?: "Makkah",
            darkMode   = prefs.getBoolean("darkMode", true)
        )
    }

    private fun updateHijri() {
        val cal  = Calendar.getInstance()
        val h    = PrayerCalculator.toHijri(cal)
        val days = listOf("الأحد","الاثنين","الثلاثاء","الأربعاء","الخميس","الجمعة","السبت")
        val greg = "${days[cal.get(Calendar.DAY_OF_WEEK) - 1]}، ${cal.get(Calendar.DAY_OF_MONTH)} / ${cal.get(Calendar.MONTH)+1} / ${cal.get(Calendar.YEAR)}"
        _state.value = _state.value.copy(hijriDay = h.first, hijriMonth = h.second, hijriYear = h.third, gregorian = greg)
    }

    fun recalcPrayers() {
        val s = _state.value
        val times = PrayerCalculator.calculate(s.lat, s.lng, method = s.calcMethod)
        _state.value = s.copy(prayerTimes = times)
    }

    private fun fixHour(h: Double): Double = h - 24.0 * Math.floor(h / 24.0)

    private fun getNextPrayer(): Pair<String, Double>? {
        val now = Calendar.getInstance()
        val nowH = now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) / 60.0 + now.get(Calendar.SECOND) / 3600.0
        val prayers = _state.value.prayerTimes.filter { it.nameAr in SALAH }
        for (p in prayers) {
            val lt = fixHour(p.decimalLocal)
            if (lt > nowH) return Pair(p.nameAr, lt)
        }
        val fajr = prayers.firstOrNull { it.nameAr == "الفجر" }
        return fajr?.let { Pair(it.nameAr, fixHour(it.decimalLocal) + 24.0) }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                val next = getNextPrayer()
                if (next != null) {
                    val now = Calendar.getInstance()
                    val nowS = now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND)
                    var diff = (next.second * 3600 - nowS).toLong()
                    if (diff < 0) diff += 86400L
                    val h = diff / 3600; val m = (diff % 3600) / 60; val s = diff % 60
                    _state.value = _state.value.copy(
                        countdown  = "%02d:%02d:%02d".format(h, m, s),
                        nextPrayer = next.first
                    )
                }
                delay(1000)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchLocation(context: Context) {
        try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val listener = object : LocationListener {
                override fun onLocationChanged(loc: Location) {
                    lm.removeUpdates(this)
                    val lat = loc.latitude; val lng = loc.longitude
                    prefs.edit().putFloat("lat", lat.toFloat()).putFloat("lng", lng.toFloat()).apply()
                    _state.value = _state.value.copy(lat = lat, lng = lng, locationOk = true)
                    recalcPrayers()
                }
                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(p: String?, s: Int, extras: Bundle?) {}
            }
            val provider = when {
                lm.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                else -> null
            }
            provider?.let { lm.requestLocationUpdates(it, 0L, 0f, listener) }
        } catch (_: Exception) {}
    }

    fun toggleDarkMode() {
        val newDark = !_state.value.darkMode
        prefs.edit().putBoolean("darkMode", newDark).apply()
        _state.value = _state.value.copy(darkMode = newDark)
    }

    override fun onCleared() { countdownJob?.cancel(); super.onCleared() }
}
