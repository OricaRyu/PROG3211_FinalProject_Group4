package com.example.finalprojectgroup4.features.splash

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finalprojectgroup4.R
import com.example.finalprojectgroup4.ui.theme.FinalProjectGroup4Theme
import kotlinx.coroutines.delay

/**
 * Logo size. Must match ic_splash_icon.xml (the logo drawn at 2x = 144dp, centred),
 * so the logo doesn't move when the system splash hands over to this screen.
 */
private val LOGO_SIZE = 144.dp

/** How long the name and tagline take to fade in. */
private const val TEXT_FADE_MS = 500

/** How long everything stays on screen before moving on. */
private const val HOLD_MS = 1000L

/**
 * Mandatory Splash. Android's system splash shows our logo first (see themes.xml);
 * this screen keeps the logo in exactly the same place and fades the name and tagline in
 * underneath, so the two look like one splash. Then the app moves on by itself.
 * All text comes from strings.xml; all colours come from our theme.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // rememberUpdatedState makes sure the latest onFinished is the one called at the end
    val currentOnFinished by rememberUpdatedState(onFinished)

    // 0f = text hidden, 1f = text fully shown.
    // Android Studio previews don't run animations, so they start fully shown.
    val isPreview = LocalInspectionMode.current
    val textAlpha = remember { Animatable(if (isPreview) 1f else 0f) }

    // Runs once when Splash first appears: fade the text in, wait, then navigate
    LaunchedEffect(Unit) {
        textAlpha.animateTo(1f, animationSpec = tween(TEXT_FADE_MS, easing = FastOutSlowInEasing))
        delay(HOLD_MS)
        currentOnFinished()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Equal weights above and below keep the logo exactly in the centre of the
            // screen, where the system splash drew it
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(R.drawable.ic_spot_logo),
                contentDescription = stringResource(R.string.cd_app_logo),
                modifier = Modifier.size(LOGO_SIZE)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer { alpha = textAlpha.value },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.splash_tagline),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SplashScreenPreview() {
    FinalProjectGroup4Theme { SplashScreen(onFinished = {}) }
}
