package com.example.finalprojectgroup4.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.finalprojectgroup4.R

/**
 * The small "Synced" / "Not synced" pill on Home and Detail.
 * Synced uses the secondary container colour; not synced uses the tertiary one so it stands out.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun SyncStatusChip(
    synced: Boolean,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = if (synced) scheme.secondaryContainer else scheme.tertiaryContainer,
        contentColor = if (synced) scheme.onSecondaryContainer else scheme.onTertiaryContainer
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (synced) Icons.Filled.Check else Icons.Filled.Refresh,
                contentDescription = null, // the text next to it says the same thing
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = stringResource(if (synced) R.string.status_synced else R.string.status_not_synced),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
