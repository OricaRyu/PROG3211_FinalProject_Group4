package com.example.finalprojectgroup4.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Temporary id used by placeholder buttons so the Detail and Edit routes can be tested
 * before real check-ins exist. Remove once the screens show real data.
 */
const val PLACEHOLDER_CHECKIN_ID = "c1" // Not a real check-in: Detail shows "not found" for it

/**
 * Temporary screen body: a centred message with optional buttons underneath.
 * Each screen replaces this with its real layout as the tracks are built.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun PlaceholderBody(
    message: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        content()
    }
}
