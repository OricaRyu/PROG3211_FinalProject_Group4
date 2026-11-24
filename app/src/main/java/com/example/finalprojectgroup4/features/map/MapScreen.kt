package com.example.finalprojectgroup4.features.map

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.finalprojectgroup4.R
import com.example.finalprojectgroup4.ui.components.PLACEHOLDER_CHECKIN_ID
import com.example.finalprojectgroup4.ui.components.PlaceholderBody
import com.example.finalprojectgroup4.ui.components.SpotTopBar
import com.example.finalprojectgroup4.ui.theme.FinalProjectGroup4Theme

/**
 * Google Map with check-in and nearby venue pins.
 * Placeholder for now; Track D builds the real map.
 *
 * Kind: @Composable function. Owner: Track D (Daniel).
 */
@Composable
fun MapScreen(
    onCheckinClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { SpotTopBar(title = stringResource(R.string.title_map)) }
    ) { innerPadding ->
        PlaceholderBody(
            message = stringResource(R.string.placeholder_map),
            modifier = Modifier.padding(innerPadding)
        ) {
            Button(onClick = { onCheckinClick(PLACEHOLDER_CHECKIN_ID) }) {
                Text(stringResource(R.string.action_open_sample))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MapScreenPreview() {
    FinalProjectGroup4Theme { MapScreen(onCheckinClick = {}) }
}
