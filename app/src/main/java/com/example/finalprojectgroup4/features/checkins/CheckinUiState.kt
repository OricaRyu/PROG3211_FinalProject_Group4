package com.example.finalprojectgroup4.features.checkins

import com.example.finalprojectgroup4.data.model.Category
import com.example.finalprojectgroup4.validation.ValidationResult

/**
 * Everything the New / Edit check-in form needs to draw itself.
 * CheckinViewModel creates it; NewCheckinScreen only reads it.
 *
 * @property isEditing true on the Edit screen, false on New check-in
 * @property isLoading true while the check-in being edited is read from the database
 * @property loadFailed true when reading the check-in hit a database error; the screen offers Retry
 * @property notFound true when the check-in to edit doesn't exist anymore (for example, it was
 *   deleted before or while the form was open)
 * @property isSaving true while the save runs; the Save button is disabled meanwhile
 * @property saveFailed true when the database hit an error while saving; the form stays open to retry
 * @property rating 0 means no star picked yet
 * @property venueError the error shown under its field, or null when the field is fine
 *   (same for ratingError and noteError)
 *
 * Kind: data class. Owner: Track B (Ana).
 */
data class CheckinUiState(
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val loadFailed: Boolean = false,
    val notFound: Boolean = false,
    val isSaving: Boolean = false,
    val saveFailed: Boolean = false,

    // What the user enters
    val venueName: String = "",
    val address: String = "",
    val category: Category = Category.RESTAURANT,
    val rating: Int = 0,
    val note: String = "",

    // Inline errors, shown after Save is tapped
    val venueError: ValidationResult.Error? = null,
    val ratingError: ValidationResult.Error? = null,
    val noteError: ValidationResult.Error? = null
)