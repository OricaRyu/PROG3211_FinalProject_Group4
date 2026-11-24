package com.example.finalprojectgroup4.features.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
 * Mandatory Home: nearby venues, latest check-ins, FAB.
 * Placeholder for now; the real layout comes in M2.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun HomeScreen(
    onAddCheckin: () -> Unit,
    onCheckinClick: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SpotTopBar(
                title = stringResource(R.string.title_home),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.action_logout)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCheckin) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_new_checkin))
            }
        }
    ) { innerPadding ->
        PlaceholderBody(
            message = stringResource(R.string.placeholder_home),
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
private fun HomeScreenPreview() {
    FinalProjectGroup4Theme { HomeScreen(onAddCheckin = {}, onCheckinClick = {}, onLogout = {}) }
}
