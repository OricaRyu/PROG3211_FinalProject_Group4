package com.example.finalprojectgroup4.features.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finalprojectgroup4.R
import com.example.finalprojectgroup4.ui.theme.FinalProjectGroup4Theme
import kotlinx.coroutines.delay

/** How long the splash stays on screen before moving on. */
private const val SPLASH_DELAY_MS = 1500L

/**
 * Mandatory Splash: logo, name and tagline, then moves on by itself after a short delay.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // rememberUpdatedState makes sure the latest onFinished is the one called after the delay
    val currentOnFinished by rememberUpdatedState(onFinished)

    // Runs once when Splash first appears
    LaunchedEffect(Unit) {
        delay(SPLASH_DELAY_MS)
        currentOnFinished()
    }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Placeholder logo until the real Spot logo is added to res/drawable
            Icon(
                imageVector = Icons.Filled.Place,
                contentDescription = stringResource(R.string.cd_app_logo),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(96.dp)
            )
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    FinalProjectGroup4Theme { SplashScreen(onFinished = {}) }
}
