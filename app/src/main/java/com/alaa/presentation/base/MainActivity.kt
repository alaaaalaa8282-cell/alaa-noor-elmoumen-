package com.alaa.presentation.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alaa.presentation.navigation.Screen
import com.alaa.presentation.screen.azkar.AzkarScreen
import com.alaa.presentation.screen.challenges.ChallengesScreen
import com.alaa.presentation.screen.dhikr.DhikrScreen
import com.alaa.presentation.screen.home.HomeScreen
import com.alaa.presentation.screen.qibla.QiblaScreen
import com.alaa.presentation.screen.quran.QuranScreen
import com.alaa.presentation.screen.settings.SettingsScreen
import com.alaa.presentation.screen.tasbih.TasbihScreen
import com.alaa.presentation.theme.*

// ── Bottom Nav Item definition ────────────────────────────────────
data class BottomNavItem(
    val screen  : Screen,
    val label   : String,
    val icon    : ImageVector,
    val emoji   : String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home,       "الصلاة",  Icons.Default.AccessTime,  "🕌"),
    BottomNavItem(Screen.Qibla,      "القبلة",  Icons.Default.Explore,     "🧭"),
    BottomNavItem(Screen.Azkar,      "الأذكار", Icons.Default.AutoStories, "📿"),
    BottomNavItem(Screen.Dhikr,      "صوتي",    Icons.Default.VolumeUp,    "🎙️"),
    BottomNavItem(Screen.Quran,      "القرآن",  Icons.Default.MenuBook,    "📖"),
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoorTheme(darkTheme = true) {
                NoorApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoorApp() {
    val navController = rememberNavController()
    val navBackStack  by navController.currentBackStackEntryAsState()
    val currentDest   = navBackStack?.destination

    // Routes that show the bottom bar
    val bottomBarRoutes = bottomNavItems.map { it.screen.route } +
            listOf(Screen.Tasbih.route, Screen.Challenges.route, Screen.Settings.route)

    Scaffold(
        containerColor = Color(0xFF010F0A),
        bottomBar = {
            if (currentDest?.route in bottomBarRoutes) {
                NoorBottomBar(
                    navController    = navController,
                    currentDestination = currentDest,
                    onMoreClick      = { navController.navigate(Screen.Settings.route) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(innerPadding),
            enterTransition  = { slideInHorizontally(tween(250)) { it } + fadeIn(tween(250)) },
            exitTransition   = { slideOutHorizontally(tween(200)) { -it } + fadeOut(tween(200)) },
            popEnterTransition  = { slideInHorizontally(tween(250)) { -it } + fadeIn(tween(250)) },
            popExitTransition   = { slideOutHorizontally(tween(200)) { it } + fadeOut(tween(200)) }
        ) {
            composable(Screen.Home.route)       { HomeScreen() }
            composable(Screen.Qibla.route)      { QiblaScreen() }
            composable(Screen.Azkar.route)      { AzkarScreen() }
            composable(Screen.Dhikr.route)      { DhikrScreen() }
            composable(Screen.Quran.route)      { QuranScreen() }
            composable(Screen.Tasbih.route)     { TasbihScreen() }
            composable(Screen.Challenges.route) { ChallengesScreen() }
            composable(Screen.Settings.route)   { SettingsScreen() }
        }
    }
}

@Composable
private fun NoorBottomBar(
    navController     : androidx.navigation.NavController,
    currentDestination: androidx.navigation.NavDestination?,
    onMoreClick       : () -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF022C22),
        tonalElevation = 0.dp,
        modifier       = Modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
            NavigationBarItem(
                selected  = selected,
                onClick   = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                icon      = {
                    Text(item.emoji, fontSize = if (selected) 22.sp else 18.sp)
                },
                label     = {
                    Text(item.label, fontSize = 10.sp, color = if (selected) Gold else TextSecondary)
                },
                colors    = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Gold,
                    unselectedIconColor = TextSecondary,
                    indicatorColor      = GoldDim
                )
            )
        }

        // More → Settings/Tasbih/Challenges
        NavigationBarItem(
            selected  = currentDestination?.route in listOf(Screen.Settings.route, Screen.Tasbih.route, Screen.Challenges.route),
            onClick   = onMoreClick,
            icon      = { Text("⚙️", fontSize = 18.sp) },
            label     = { Text("المزيد", fontSize = 10.sp, color = TextSecondary) },
            colors    = NavigationBarItemDefaults.colors(
                selectedIconColor   = Gold,
                unselectedIconColor = TextSecondary,
                indicatorColor      = GoldDim
            )
        )
    }
}
