package com.example.finalprojectgroup4.features.checkins

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
 * Mandatory List: check-ins in a LazyColumn with filter chips and FAB.
 * Placeholder for now; the LazyColumn comes in M2 once Track B's data is ready.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun CheckinListScreen(
    onAddCheckin: () -> Unit,
    onCheckinClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { SpotTopBar(title = stringResource(R.string.title_checkins)) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCheckin) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_new_checkin))
            }
        }
    ) { innerPadding ->
        PlaceholderBody(
            message = stringResource(R.string.placeholder_checkins),
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
private fun CheckinListScreenPreview() {
    FinalProjectGroup4Theme { CheckinListScreen(onAddCheckin = {}, onCheckinClick = {}) }
}
