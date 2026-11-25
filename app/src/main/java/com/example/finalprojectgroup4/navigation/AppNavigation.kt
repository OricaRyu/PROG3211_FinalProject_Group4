package com.example.finalprojectgroup4.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.finalprojectgroup4.features.auth.LoginScreen
import com.example.finalprojectgroup4.features.auth.SignUpScreen
import com.example.finalprojectgroup4.features.checkins.CheckinDetailScreen
import com.example.finalprojectgroup4.features.checkins.CheckinListScreen
import com.example.finalprojectgroup4.features.checkins.NewCheckinScreen
import com.example.finalprojectgroup4.features.home.HomeScreen
import com.example.finalprojectgroup4.features.map.MapScreen
import com.example.finalprojectgroup4.features.splash.SplashScreen

/**
 * The whole navigation graph: one NavHost with bottom tabs (Home, Map, Check-ins)
 * and stack screens (Splash, Login, Sign up, Detail, New, Edit).
 *
 * Screens never receive the NavController. They get lambdas such as onAddCheckin,
 * and this file decides where each one goes.
 *
 * Layout: a plain Column (no Scaffold here). The NavHost fills the space and the bottom bar
 * sits under it on the three tabs. Each screen owns its own Scaffold and window insets, so
 * screens that want to draw edge-to-edge (Splash, and later the Map) can.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    // Watch the current route so the bottom bar knows which tab is selected
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = BottomTab.entries.any { it.route == currentRoute }

    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier
                .weight(1f)
                // When the bottom bar is showing, it already pads itself above the system
                // navigation bar, so the screen above it must not add that space again.
                // The status bar inset is never touched here; each screen handles its own.
                .then(
                    if (showBottomBar) Modifier.consumeWindowInsets(WindowInsets.navigationBars)
                    else Modifier
                )
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(
                    onFinished = {
                        // TODO (Track C): go to Routes.LOGIN instead when no user is signed in
                        navController.navigate(Routes.HOME) {
                            // Remove Splash so Back does not return to it
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoggedIn = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onCreateAccount = { navController.navigate(Routes.SIGN_UP) }
                )
            }

            composable(Routes.SIGN_UP) {
                SignUpScreen(
                    onSignedUp = {
                        navController.navigate(Routes.HOME) {
                            // Removes both Sign up and Login from the back stack
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onBackToLogin = { navController.popBackStack() }
                )
            }

            composable(Routes.HOME) {
                HomeScreen(
                    onAddCheckin = { navController.navigate(Routes.NEW) },
                    // TODO (Track B/D): pass the venue to New check-in so its fields are filled in
                    onVenueCheckIn = { navController.navigate(Routes.NEW) },
                    onCheckinClick = { id -> navController.navigate(Routes.detail(id)) },
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            // Clear the whole back stack so Back cannot return to signed-in screens
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.MAP) {
                MapScreen(
                    onCheckinClick = { id -> navController.navigate(Routes.detail(id)) }
                )
            }

            composable(Routes.CHECKINS) {
                CheckinListScreen(
                    onAddCheckin = { navController.navigate(Routes.NEW) },
                    onCheckinClick = { id -> navController.navigate(Routes.detail(id)) }
                )
            }

            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument(Routes.ARG_ID) { type = NavType.StringType })
            ) {
                CheckinDetailScreen(
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(Routes.edit(id)) },
                    onDeleted = { navController.popBackStack() }
                )
            }

            composable(Routes.NEW) {
                NewCheckinScreen(
                    checkinId = null,
                    onClose = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.EDIT,
                arguments = listOf(navArgument(Routes.ARG_ID) { type = NavType.StringType })
            ) { entry ->
                NewCheckinScreen(
                    checkinId = entry.arguments?.getString(Routes.ARG_ID),
                    onClose = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
        }

        if (showBottomBar) {
            SpotBottomBar(
                currentRoute = currentRoute,
                onTabSelected = { tab -> navController.navigateToTab(tab.route) }
            )
        }
    }
}

/** The bottom navigation bar, built from the BottomTab enum. */
@Composable
private fun SpotBottomBar(
    currentRoute: String?,
    onTabSelected: (BottomTab) -> Unit
) {
    NavigationBar {
        BottomTab.entries.forEach { tab ->
            val label = stringResource(tab.labelRes)
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = { onTabSelected(tab) },
                // The label below already names the tab, so TalkBack shouldn't read it twice
                icon = { Icon(tab.icon, contentDescription = null) },
                label = { Text(label) }
            )
        }
    }
}

/**
 * Standard tab switch: keeps Home at the bottom of the back stack, avoids stacking
 * duplicate copies of a tab, and restores each tab's state when you come back to it.
 */
private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
