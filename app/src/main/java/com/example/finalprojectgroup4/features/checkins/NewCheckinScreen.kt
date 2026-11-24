package com.example.finalprojectgroup4.features.checkins

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.finalprojectgroup4.R
import com.example.finalprojectgroup4.ui.components.PlaceholderBody
import com.example.finalprojectgroup4.ui.components.SpotTopBar
import com.example.finalprojectgroup4.ui.theme.FinalProjectGroup4Theme

/**
 * New / Edit check-in form. One screen handles both:
 * checkinId == null means a new check-in, otherwise it edits that check-in.
 * Placeholder for now; Track B builds the real form and validation.
 *
 * Kind: @Composable function. Owner: Track B (Ana).
 */
@Composable
fun NewCheckinScreen(
    checkinId: String?,
    onClose: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val titleRes = if (checkinId == null) R.string.title_new_checkin else R.string.title_edit_checkin

    Scaffold(
        modifier = modifier,
        topBar = {
            SpotTopBar(
                title = stringResource(titleRes),
                navigationIcon = Icons.Filled.Close,
                navigationContentDescription = stringResource(R.string.action_close),
                onNavigationClick = onClose
            )
        }
    ) { innerPadding ->
        PlaceholderBody(
            message = stringResource(R.string.placeholder_form),
            modifier = Modifier.padding(innerPadding)
        ) {
            Button(onClick = onSaved) {
                Text(stringResource(R.string.action_save))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NewCheckinScreenPreview() {
    FinalProjectGroup4Theme { NewCheckinScreen(checkinId = null, onClose = {}, onSaved = {}) }
}
