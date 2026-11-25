package com.example.finalprojectgroup4.features.checkins

import android.content.res.Configuration
import android.text.format.DateFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalprojectgroup4.R
import com.example.finalprojectgroup4.data.model.Checkin
import com.example.finalprojectgroup4.data.sample.SampleCheckinStore
import com.example.finalprojectgroup4.ui.components.RatingStars
import com.example.finalprojectgroup4.ui.components.SpotTopBar
import com.example.finalprojectgroup4.ui.components.SyncStatusChip
import com.example.finalprojectgroup4.ui.components.colors
import com.example.finalprojectgroup4.ui.components.labelRes
import com.example.finalprojectgroup4.ui.theme.FinalProjectGroup4Theme
import java.util.Date

/**
 * Mandatory Detail: one check-in with all its details, Edit, and Delete with a confirmation dialog.
 *
 * This part gets the ViewModel and reads its state; CheckinDetailContent below only draws it.
 * The check-in id reaches the ViewModel from the route, so it isn't a parameter here.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun CheckinDetailScreen(
    onBack: () -> Unit,
    onEdit: (checkinId: String) -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CheckinDetailViewModel = viewModel()
) {
    // Stops collecting while the app is in the background
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDeleted by viewModel.isDeleted.collectAsStateWithLifecycle()

    // Leave once the check-in is deleted. The key only turns true once, so this pops
    // exactly one screen even if Delete is confirmed again during the exit animation.
    LaunchedEffect(isDeleted) {
        if (isDeleted) onDeleted()
    }

    // dropUnlessResumed ignores Back and Edit once this screen starts leaving (its exit
    // animation still shows the buttons). Without it, Back right after Delete would pop
    // twice and also close the screen underneath.
    CheckinDetailContent(
        uiState = uiState,
        onBack = dropUnlessResumed(block = onBack),
        // Edit the check-in that is actually shown, not an id read separately from the route
        onEdit = dropUnlessResumed {
            (uiState as? CheckinDetailUiState.Loaded)?.let { onEdit(it.checkin.id) }
        },
        // Not guarded: confirming must always delete. Navigation happens in the effect above.
        onConfirmDelete = viewModel::delete,
        modifier = modifier
    )
}

/**
 * Draws the Detail screen from its state. No ViewModel, no data loading: just layout.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun CheckinDetailContent(
    uiState: CheckinDetailUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Whether the "Delete check-in?" dialog is open. rememberSaveable keeps it open on rotation.
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val loaded = uiState as? CheckinDetailUiState.Loaded

    Scaffold(
        modifier = modifier,
        topBar = {
            SpotTopBar(
                title = stringResource(R.string.title_checkin_detail),
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                navigationContentDescription = stringResource(R.string.action_back),
                onNavigationClick = onBack,
                actions = {
                    // Only offer Edit and Delete when there is a check-in to act on
                    if (loaded != null) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (uiState) {
            CheckinDetailUiState.Loading -> Box(contentModifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            CheckinDetailUiState.NotFound -> NotFoundMessage(onBack = onBack, modifier = contentModifier)

            is CheckinDetailUiState.Loaded -> CheckinDetails(
                checkin = uiState.checkin,
                onEdit = onEdit,
                onDelete = { showDeleteDialog = true },
                modifier = contentModifier
            )
        }
    }

    if (showDeleteDialog && loaded != null) {
        DeleteConfirmationDialog(
            venueName = loaded.checkin.venueName,
            onConfirm = {
                showDeleteDialog = false
                onConfirmDelete()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

/** All the check-in's details, laid out like the mockup. Scrolls on small screens. */
@Composable
private fun CheckinDetails(
    checkin: Checkin,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Map / photo area. TODO (Track D): small map preview at the venue's location
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Place,
                contentDescription = null, // decorative placeholder
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp)
            )
        }

        // Name and address
        Column {
            Text(
                text = checkin.venueName,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            checkin.address?.let { address ->
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Category and rating
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryBadge(checkin = checkin)
            RatingStars(rating = checkin.rating)
        }

        // When it was checked in, and when it was last edited (only if it was)
        Column {
            Text(
                text = stringResource(R.string.detail_checked_in, formatDateTime(checkin.createdAt)),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (checkin.updatedAt > checkin.createdAt) {
                Text(
                    text = stringResource(R.string.detail_edited, formatDateTime(checkin.updatedAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Note
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.detail_note_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                val note = checkin.note
                Text(
                    text = if (note.isNullOrBlank()) stringResource(R.string.detail_no_note) else note,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (note.isNullOrBlank()) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        SyncStatusChip(synced = checkin.synced)

        // Main actions, as in the mockup (also available as icons in the top bar)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.action_edit))
            }
            Button(
                onClick = onDelete,
                modifier = Modifier.weight(1f),
                // Error colour marks it as a destructive action
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(stringResource(R.string.action_delete))
            }
        }
    }
}

/** Filled pill with the category name in its category colour, like "Restaurant" in the mockup. */
@Composable
private fun CategoryBadge(checkin: Checkin) {
    val categoryColors = checkin.category.colors()
    Surface(
        shape = CircleShape,
        color = categoryColors.container,
        contentColor = categoryColors.content
    ) {
        Text(
            text = stringResource(checkin.category.labelRes()),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/** Shown when the id doesn't match any check-in. */
@Composable
private fun NotFoundMessage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.detail_not_found),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedButton(onClick = onBack) {
            Text(stringResource(R.string.action_back))
        }
    }
}

/** "Delete check-in?" confirmation. Nothing is deleted unless the user taps Delete. */
@Composable
private fun DeleteConfirmationDialog(
    venueName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
        title = { Text(stringResource(R.string.delete_dialog_title)) },
        text = { Text(stringResource(R.string.delete_dialog_message, venueName)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.action_delete),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

/** Full date and time such as "Sun, Nov 8, 2026, 7:30 PM", in the phone's language and 12/24 h setting. */
@Composable
private fun formatDateTime(millis: Long): String {
    val locale = LocalConfiguration.current.locales[0]
    // The user's 12/24 h toggle; the skeleton letter "j" would only use the locale's default
    val is24Hour = DateFormat.is24HourFormat(LocalContext.current)
    return remember(millis, locale, is24Hour) {
        val skeleton = if (is24Hour) "EEEMMMdyyyyHmm" else "EEEMMMdyyyyhmm"
        val pattern = DateFormat.getBestDateTimePattern(locale, skeleton)
        DateFormat.format(pattern, Date(millis)).toString()
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CheckinDetailPreview() {
    FinalProjectGroup4Theme {
        CheckinDetailContent(
            uiState = CheckinDetailUiState.Loaded(SampleCheckinStore.checkins.value.first()),
            onBack = {},
            onEdit = {},
            onConfirmDelete = {}
        )
    }
}

@Preview(name = "Not found", showBackground = true)
@Composable
private fun CheckinDetailNotFoundPreview() {
    FinalProjectGroup4Theme {
        CheckinDetailContent(
            uiState = CheckinDetailUiState.NotFound,
            onBack = {},
            onEdit = {},
            onConfirmDelete = {}
        )
    }
}
