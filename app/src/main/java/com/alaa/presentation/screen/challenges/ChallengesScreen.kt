package com.alaa.presentation.screen.challenges

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alaa.presentation.theme.*

private data class Challenge(val day: Int, val title: String, val desc: String, val reward: String, val emoji: String)

private val CHALLENGES = listOf(
    Challenge(1,  "صلاة الفجر في وقتها",    "صلِّ الفجر في وقته اليوم واستقبل يومك بالخير",     "ملائكة الليل والنهار تشهد لك",       "🌅"),
    Challenge(2,  "قراءة سورة الكهف",       "اقرأ سورة الكهف كاملة — 110 آية",                   "نور يضيء بين الجمعتين",               "📖"),
    Challenge(3,  "100 استغفار",            "قل أستغفر الله مائة مرة على مدار اليوم",            "يرفع عنك الهم ويوسع رزقك",            "🤲"),
    Challenge(4,  "صدقة اليوم",             "تصدق بأي شيء ولو بكلمة طيبة",                       "الصدقة تطفئ غضب الرب",                "💝"),
    Challenge(5,  "صلاة الضحى",             "صلِّ ركعتين من الضحى بعد شروق الشمس بربع ساعة",    "كصدقة عن كل مفصل في جسدك",           "☀️"),
    Challenge(6,  "تلاوة ربع جزء",         "اقرأ ربع جزء من القرآن الكريم بتأمل وتدبر",         "بكل حرف عشر حسنات",                  "📗"),
    Challenge(7,  "أذكار الصباح كاملة",     "أكمل جميع أذكار الصباح بتدبر وحضور قلب",            "في حفظ الله حتى المساء",              "🌄"),
    Challenge(8,  "زيارة أو اتصال بالأهل",  "تواصل مع أحد والديك أو قريبك اليوم",               "صلة الرحم تزيد الرزق وتطيل العمر",   "👨‍👩‍👧"),
    Challenge(9,  "صيام يوم تطوع",         "أمسك اليوم نية لله من باب التطوع",                   "الصوم جنة من النار",                  "🌙"),
    Challenge(10, "ختمة صلاة على النبي",    "صلِّ على النبي ﷺ مائة مرة اليوم",                  "من صلى علي مائة كُتبت له مائة حاجة", "💫"),
    Challenge(11, "حفظ آية جديدة",          "احفظ آية لم تحفظها من قبل وراجعها ثلاث مرات",       "أهل القرآن أهل الله وخاصته",          "🧠"),
    Challenge(12, "أذكار المساء كاملة",     "أكمل جميع أذكار المساء بتدبر وحضور قلب",            "في حفظ الله حتى الصباح",              "🌙"),
    Challenge(13, "الصلاة في المسجد",       "أدِّ صلاة أو أكثر في المسجد اليوم",                 "الصلاة في المسجد بسبع وعشرين درجة",  "🕌"),
    Challenge(14, "إطعام محتاج",            "أطعم محتاجاً أو تصدق بطعام اليوم",                  "أجر عظيم عند الله",                   "🍲"),
    Challenge(15, "ختم حزب من القرآن",      "اقرأ حزباً كاملاً من القرآن بتدبر",                 "القرآن شفاء لما في الصدور",            "📖"),
    Challenge(16, "قيام الليل ركعتين",      "صلِّ ركعتين في الثلث الأخير من الليل",              "أقرب ما يكون العبد من ربه في السجود", "🌃"),
    Challenge(17, "إزالة الأذى عن الطريق",  "أزِل شيئاً يؤذي المارة من طريق",                   "شعبة من الإيمان",                     "🛤️"),
    Challenge(18, "تسبيح ألف مرة",         "قل سبحان الله ألف مرة على مدار اليوم",              "تملأ الميزان",                        "📿"),
    Challenge(19, "صلاة السنن الرواتب",     "صلِّ جميع الرواتب مع الفرائض اليوم (12 ركعة)",      "بيت في الجنة لمن يواظب عليها",       "🕋"),
    Challenge(20, "دعاء من القلب",          "اجلس خمس دقائق وادعُ الله من قلبك بما تشاء",       "الدعاء مخ العبادة",                   "🤲"),
    Challenge(21, "ابتسامة لكل مسلم",      "ابتسم في وجه كل من تلقاه اليوم",                    "الابتسامة صدقة",                      "😊"),
    Challenge(22, "استماع لمحاضرة دينية",   "استمع لمحاضرة أو خطبة مفيدة اليوم",                 "طلب العلم فريضة",                     "🎧"),
    Challenge(23, "إصلاح ذات البين",        "أصلح بين متخاصمين أو أرسل كلمة صلح",               "أفضل من صلاة النافلة والصدقة",        "🕊️"),
    Challenge(24, "التوبة والاستغفار",       "تب إلى الله توبة صادقة واستغفر ألف مرة",           "الله يحب التوابين",                    "✨"),
    Challenge(25, "زيارة مريض",             "زر مريضاً أو اطمئن عليه",                            "للزائر خريف الجنة",                   "💚"),
    Challenge(26, "ختمة الإخلاص والمعوذتين","اقرأ المعوذات ثلاث مرات صباحاً ومساءً",             "كفتاه من كل شيء",                    "🛡️"),
    Challenge(27, "إعانة محتاج",            "أعِن شخصاً يحتاج مساعدة اليوم",                     "الله في عون العبد ما كان في عون أخيه","🤝"),
    Challenge(28, "آية الكرسي كل صلاة",    "اقرأ آية الكرسي بعد كل صلاة مكتوبة",               "لم يكن بينه وبين الجنة إلا الموت",   "⭐"),
    Challenge(29, "تهجد وقيام",            "قم الليل وصلِّ على الأقل أربع ركعات",              "شرف المؤمن قيامه بالليل",             "🌙"),
    Challenge(30, "يوم الشكر",              "اقضِ يومك كله شاكراً لله على كل نعمة",              "لئن شكرتم لأزيدنكم",                 "🌟")
)

@Composable
fun ChallengesScreen() {
    val context = LocalContext.current
    val prefs   = context.getSharedPreferences("challenges_prefs", Context.MODE_PRIVATE)
    val checked = remember { mutableStateMapOf<Int, Boolean>().also { map -> CHALLENGES.forEach { c -> map[c.day] = prefs.getBoolean("day_${c.day}", false) } } }
    val total   = CHALLENGES.size
    val done    = checked.values.count { it }
    val pct     = if (total > 0) done * 100 / total else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF022C22), Color(0xFF010F0A))))
    ) {
        // ── Header ────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 4.dp)
        ) {
            Text("🏆 تحدي 30 يوماً", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Gold)
            Text("30-Day Islamic Challenge", fontSize = 11.sp, color = TextSecondary, letterSpacing = 1.sp)
        }

        // ── Progress ──────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
            colors   = CardDefaults.cardColors(containerColor = CardDark),
            shape    = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("$done / $total يوم مكتمل", fontSize = 13.sp, color = TextSecondary)
                    Text("$pct%", fontSize = 14.sp, color = Gold, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { done.toFloat() / total },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color    = GreenLight, trackColor = Color(0x33064E3B)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    when { pct == 100 -> "🎉 ما شاء الله! أكملت التحدي بالكامل!"
                        pct >= 75   -> "⭐ ممتاز! أنت في المراحل الأخيرة"
                        pct >= 50   -> "💪 في المنتصف — استمر!"
                        pct >= 25   -> "🌱 بداية جيدة — ثابر!"
                        else        -> "ابدأ رحلتك مع الله اليوم 🌟" },
                    fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ── List ──────────────────────────────────────────────────
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(CHALLENGES) { _, ch ->
                val isDone = checked[ch.day] == true
                ChallengeCard(ch = ch, isDone = isDone, onToggle = {
                    val next = !isDone
                    checked[ch.day] = next
                    prefs.edit().putBoolean("day_${ch.day}", next).apply()
                })
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun ChallengeCard(ch: Challenge, isDone: Boolean, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle),
        colors   = CardDefaults.cardColors(containerColor = if (isDone) Color(0x44064E3B) else CardDark),
        shape    = RoundedCornerShape(14.dp),
        border   = androidx.compose.foundation.BorderStroke(
            1.dp, if (isDone) GreenLight.copy(0.5f) else Color(0x33C9A84C)
        )
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Day badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(46.dp)
                    .background(if (isDone) GreenDim else GoldDim, CircleShape)
                    .border(1.5.dp, if (isDone) GreenLight else Gold, CircleShape)
            ) {
                if (isDone) Icon(Icons.Default.Check, null, tint = GreenLight, modifier = Modifier.size(22.dp))
                else Text("${ch.day}", fontSize = 15.sp, color = Gold, fontWeight = FontWeight.Black)
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(ch.emoji, fontSize = 16.sp)
                    Text(ch.title, fontSize = 14.sp, color = if (isDone) GreenLight else TextPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(3.dp))
                Text(ch.desc, fontSize = 11.sp, color = TextSecondary)
                Spacer(Modifier.height(3.dp))
                Text("💎 ${ch.reward}", fontSize = 10.sp, color = Gold.copy(0.8f))
            }
        }
    }
}
