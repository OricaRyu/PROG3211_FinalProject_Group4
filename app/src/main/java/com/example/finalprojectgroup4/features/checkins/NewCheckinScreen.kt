package com.example.finalprojectgroup4.features.checkins

import android.content.res.Configuration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalprojectgroup4.R
import com.example.finalprojectgroup4.data.model.Category
import com.example.finalprojectgroup4.ui.components.SpotTopBar
import com.example.finalprojectgroup4.ui.components.labelRes
import com.example.finalprojectgroup4.ui.theme.FinalProjectGroup4Theme
import com.example.finalprojectgroup4.validation.ValidationResult
import com.example.finalprojectgroup4.validation.Validators
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.TextButton
import androidx.compose.ui.semantics.Role

/**
 * New / Edit check-in form. One screen handles both: the route decides which
 * ("new" or "edit/{id}"), and the ViewModel reads the id from it, so it isn't a parameter here.
 *
 * This part gets the ViewModel and reads its state; NewCheckinContent below only draws it.
 *
 * Kind: @Composable function. Owner: Track B (Ana).
 */
@Composable
fun NewCheckinScreen(
    onClose: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CheckinViewModel = viewModel()
) {
    // Stops collecting while the app is in the background
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()

    // Close once Room has confirmed the save. The key only turns true once,
    // so this leaves exactly once even if Save is tapped again during the exit animation.
    LaunchedEffect(isSaved) {
        if (isSaved) onSaved()
    }

    NewCheckinContent(
        uiState = uiState,
        // dropUnlessResumed ignores Close once the screen starts leaving
        onClose = dropUnlessResumed(block = onClose),
        onVenueNameChange = viewModel::onVenueNameChange,
        onAddressChange = viewModel::onAddressChange,
        onCategoryChange = viewModel::onCategoryChange,
        onRatingChange = viewModel::onRatingChange,
        onNoteChange = viewModel::onNoteChange,
        onSave = viewModel::save,
        onRetryLoad = viewModel::retryLoad,
        modifier = modifier
    )
}

/**
 * Draws the form from its state. No ViewModel, no data loading: just layout.
 * Fields: venue (required), address, category, rating (required) and note.
 * Each error shows under the field it belongs to.
 *
 * Kind: @Composable function. Owner: Track B (Ana).
 */
@Composable
fun NewCheckinContent(
    uiState: CheckinUiState,
    onClose: () -> Unit,
    onVenueNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onCategoryChange: (Category) -> Unit,
    onRatingChange: (Int) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onRetryLoad: () -> Unit,
    modifier: Modifier = Modifier
) {
    val titleRes = if (uiState.isEditing) R.string.title_edit_checkin else R.string.title_new_checkin

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
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // Editing a check-in that was deleted meanwhile
            uiState.notFound -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.detail_not_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            // A database error while reading the check-in: offer Retry
            uiState.loadFailed -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.error_load_failed),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    TextButton(onClick = onRetryLoad) {
                        Text(stringResource(R.string.action_retry))
                    }
                }
            }

            else -> CheckinForm(
                uiState = uiState,
                onVenueNameChange = onVenueNameChange,
                onAddressChange = onAddressChange,
                onCategoryChange = onCategoryChange,
                onRatingChange = onRatingChange,
                onNoteChange = onNoteChange,
                onSave = onSave,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            )
        }
    }
}

/** The form fields and the Save button, in a column that scrolls above the keyboard. */
@Composable
private fun CheckinForm(
    uiState: CheckinUiState,
    onVenueNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onCategoryChange: (Category) -> Unit,
    onRatingChange: (Int) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding() // keeps the focused field above the keyboard
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Venue (required). In M3 it is filled from the venue search (#47).
        OutlinedTextField(
            value = uiState.venueName,
            onValueChange = onVenueNameChange,
            label = { Text(stringResource(R.string.label_venue)) },
            isError = uiState.venueError != null,
            // Always present (empty when fine), so the fields below don't jump when an error appears
            supportingText = { uiState.venueError?.let { Text(it.message()) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Address (optional)
        OutlinedTextField(
            value = uiState.address,
            onValueChange = onAddressChange,
            label = { Text(stringResource(R.string.label_address)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        // Category: exactly one chip is selected
        FormSection(title = stringResource(R.string.label_category)) {
            Row(
                // Scrolls sideways if the chips don't fit, for example with large text
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Category.entries.forEach { category ->
                    FilterChip(
                        selected = uiState.category == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(stringResource(category.labelRes())) }
                    )
                }
            }
        }

        // Rating (required)
        FormSection(title = stringResource(R.string.label_rating)) {
            RatingPicker(rating = uiState.rating, onRatingChange = onRatingChange)
            uiState.ratingError?.let { ErrorText(it.message()) }
        }

        // Note (optional). Below it: the error if too long, otherwise a "45 / 200" counter.
        OutlinedTextField(
            value = uiState.note,
            onValueChange = onNoteChange,
            label = { Text(stringResource(R.string.label_note)) },
            isError = uiState.noteError != null,
            supportingText = {
                Text(
                    uiState.noteError?.message()
                        ?: stringResource(
                            R.string.format_note_counter,
                            uiState.note.length,
                            Validators.MAX_NOTE_LENGTH
                        )
                )
            },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.saveFailed) {
            ErrorText(stringResource(R.string.error_save_failed))
        }

        Button(
            onClick = onSave,
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.action_save))
        }
    }
}

/** A small title above a group of controls (Category, Rating). */
@Composable
private fun FormSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        content()
    }
}

/** Red helper text, in the same style as a text field's error. */
@Composable
private fun ErrorText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error
    )
}

/**
 * Five tappable stars: tapping star 3 sets the rating to 3.
 * Same colours as RatingStars: filled stars in primary, the rest in outline variant.
 *
 * The stars work as a radio group, so TalkBack says which one is selected
 * ("Rate 3 out of 5, selected") instead of relying on colour alone.
 */
@Composable
private fun RatingPicker(rating: Int, onRatingChange: (Int) -> Unit) {
    Row(Modifier.selectableGroup()) {
        for (star in Validators.MIN_RATING..Validators.MAX_RATING) {
            Box(
                modifier = Modifier
                    .size(48.dp) // minimum touch target
                    .selectable(
                        selected = star == rating,
                        onClick = { onRatingChange(star) },
                        role = Role.RadioButton
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    // Each star says what tapping it does, e.g. "Rate 3 out of 5"
                    contentDescription = stringResource(R.string.cd_rate_stars, star, Validators.MAX_RATING),
                    tint = if (star <= rating) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/** The error's message from strings.xml, with its placeholders (%1$d...) filled in. */
@Composable
private fun ValidationResult.Error.message(): String =
    stringResource(messageRes, *formatArgs.toTypedArray())

@Preview(name = "New", showBackground = true)
@Composable
private fun NewCheckinPreview() {
    FinalProjectGroup4Theme {
        NewCheckinContent(
            uiState = CheckinUiState(),
            onClose = {}, onVenueNameChange = {}, onAddressChange = {}, onCategoryChange = {},
            onRatingChange = {}, onNoteChange = {}, onSave = {}, onRetryLoad = {}
        )
    }
}

@Preview(name = "Edit", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EditCheckinPreview() {
    FinalProjectGroup4Theme {
        NewCheckinContent(
            uiState = CheckinUiState(
                isEditing = true,
                venueName = "Maple Bistro",
                address = "123 Queen St, Waterloo",
                category = Category.RESTAURANT,
                rating = 4,
                note = "Great pasta, will come back with the team."
            ),
            onClose = {}, onVenueNameChange = {}, onAddressChange = {}, onCategoryChange = {},
            onRatingChange = {}, onNoteChange = {}, onSave = {}, onRetryLoad = {}
        )
    }
}

@Preview(name = "Errors after Save", showBackground = true)
@Composable
private fun CheckinErrorsPreview() {
    FinalProjectGroup4Theme {
        NewCheckinContent(
            uiState = CheckinUiState(
                venueError = ValidationResult.Error(R.string.error_venue_required),
                ratingError = ValidationResult.Error(
                    R.string.error_rating_range,
                    listOf(Validators.MIN_RATING, Validators.MAX_RATING)
                )
            ),
            onClose = {}, onVenueNameChange = {}, onAddressChange = {}, onCategoryChange = {},
            onRatingChange = {}, onNoteChange = {}, onSave = {}, onRetryLoad = {}
        )
    }
}

@Preview(name = "Load failed", showBackground = true)
@Composable
private fun CheckinLoadFailedPreview() {
    FinalProjectGroup4Theme {
        NewCheckinContent(
            uiState = CheckinUiState(isEditing = true, loadFailed = true),
            onClose = {}, onVenueNameChange = {}, onAddressChange = {}, onCategoryChange = {},
            onRatingChange = {}, onNoteChange = {}, onSave = {}, onRetryLoad = {}
        )
    }
}