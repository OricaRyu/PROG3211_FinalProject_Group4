package com.example.finalprojectgroup4.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.finalprojectgroup4.R

/**
 * The bottom navigation bar's tabs. Each tab carries its route, label and icon,
 * so the bar can be built by looping over BottomTab.entries.
 *
 * Kind: enum class. Owner: Track A (Sébastien).
 */
enum class BottomTab(
    val route: String,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
) {
    HOME(Routes.HOME, R.string.tab_home, Icons.Filled.Home),
    MAP(Routes.MAP, R.string.tab_map, Icons.Filled.Place),
    CHECKINS(Routes.CHECKINS, R.string.tab_checkins, Icons.AutoMirrored.Filled.List)
}
