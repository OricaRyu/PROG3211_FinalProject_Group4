package com.example.finalprojectgroup4.features.checkins

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
 * Mandatory Detail: one check-in with Edit and Delete.
 * Placeholder for now; the full layout and delete confirmation come in M2.
 *
 * @param checkinId the id taken from the route "checkin/{id}"
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun CheckinDetailScreen(
    checkinId: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SpotTopBar(
                title = stringResource(R.string.title_checkin_detail),
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                navigationContentDescription = stringResource(R.string.action_back),
                onNavigationClick = onBack,
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                    }
                    // TODO (M2): ask for confirmation before deleting
                    IconButton(onClick = onDeleted) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                    }
                }
            )
        }
    ) { innerPadding ->
        PlaceholderBody(
            message = stringResource(R.string.placeholder_detail, checkinId),
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CheckinDetailScreenPreview() {
    FinalProjectGroup4Theme {
        CheckinDetailScreen(checkinId = PLACEHOLDER_CHECKIN_ID, onBack = {}, onEdit = {}, onDeleted = {})
    }
}
