package com.example.finalprojectgroup4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.finalprojectgroup4.navigation.AppNavigation
import com.example.finalprojectgroup4.ui.theme.FinalProjectGroup4Theme

/**
 * The app's single Activity. It applies the theme and hands control to AppNavigation;
 * every screen is a composable inside the NavHost.
 *
 * Kind: class extending ComponentActivity. Owner: Track A (Sébastien).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Shows the system splash (our logo on our background) while the app starts, then
        // switches to the normal theme. Must be called before super.onCreate.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinalProjectGroup4Theme {
                AppNavigation()
            }
        }
    }
}
