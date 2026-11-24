package com.example.finalprojectgroup4.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * The top app bar used by every screen, so titles and back/close buttons look the same everywhere.
 *
 * @param navigationIcon optional icon on the left (back arrow or close X); null shows nothing
 * @param actions optional icon buttons on the right (Edit, Delete, Log out...)
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
    navigationContentDescription: String? = null,
    onNavigationClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        modifier = modifier,
        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(navigationIcon, contentDescription = navigationContentDescription)
                }
            }
        },
        actions = actions,
        // Titles in our brand colour; icons stay in the regular on-surface colour
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
