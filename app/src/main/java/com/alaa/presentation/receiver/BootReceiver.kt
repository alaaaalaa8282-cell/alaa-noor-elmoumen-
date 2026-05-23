package com.alaa.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.alaa.R
import com.alaa.domain.usecase.PrayerSchedulingUseCase
import com.alaa.presentation.base.MainActivity
import com.alaa.presentation.service.DhikrService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── 1. إعادة جدولة الأذان ──────────────────────────────────
                val useCase = GlobalContext.get().get<PrayerSchedulingUseCase>()
                useCase.rescheduleTodayPrayerAlarms()

                // ── 2. إرجاع الأذكار لو كانت شغالة ────────────────────────
                val prefs = context.getSharedPreferences("dhikr_prefs", Context.MODE_PRIVATE)
                val wasRunning = prefs.getBoolean("dhikr_was_running", false)

                if (wasRunning) {
                    val dhikrId      = prefs.getInt("dhikr_id", 1)
                    val intervalMin  = prefs.getInt("dhikr_interval", 5)
                    val volume       = prefs.getFloat("dhikr_volume", 0.8f)
                    val repeatSingle = prefs.getBoolean("dhikr_repeat_single", false)

                    val allResIds = intArrayOf(
                        R.raw.alhamdo_lelah,
                        R.raw.allahom_lk_alhamd,
                        R.raw.ayah_elahzab,
                        R.raw.lahawla_wlaqowat,
                        R.raw.nozaker_salt_ala_habib,
                        R.raw.rbna_ighfer_li,
                        R.raw.sobhanallah_wabehamdeh,
                        R.raw.allah_akbar,
                        R.raw.astaghfer_allah,
                        R.raw.la_ilah_ela_allah
                    )
                    val allTexts = arrayOf(
                        "الحمد لله", "اللهم لك الحمد", "آية الأحزاب",
                        "لا حول ولا قوة إلا بالله", "الصلاة على النبي",
                        "ربنا اغفر لي", "سبحان الله وبحمده",
                        "الله أكبر", "أستغفر الله", "لا إله إلا الله"
                    )

                    val resIds = if (repeatSingle) intArrayOf(allResIds[(dhikrId - 1).coerceIn(0, allResIds.size - 1)]) else allResIds
                    val texts  = if (repeatSingle) arrayOf(allTexts[(dhikrId - 1).coerceIn(0, allTexts.size - 1)]) else allTexts

                    val dhikrIntent = Intent(context, DhikrService::class.java).apply {
                        putExtra(DhikrService.EXTRA_RES_IDS, resIds)
                        putExtra(DhikrService.EXTRA_TEXTS, texts)
                        putExtra(DhikrService.EXTRA_INTERVAL_MINUTES, intervalMin)
                        putExtra(DhikrService.EXTRA_VOLUME, volume)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(dhikrIntent)
                    } else {
                        context.startService(dhikrIntent)
                    }
                }

                // ── 3. فتح التطبيق ──────────────────────────────────────────
                context.startActivity(
                    Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            } finally {
                pendingResult.finish()
            }
        }
    }
}
