package com.example.finalprojectgroup4.validation

import com.example.finalprojectgroup4.R

/**
 * Validation rules for the check-in form. Each function checks one field and returns
 * ValidationResult.Success, or ValidationResult.Error with a strings.xml message id.
 *
 * Kind: object. Owner: Track B (Ana), with Track C.
 */
object Validators {

    const val MIN_RATING = 1
    const val MAX_RATING = 5
    const val MAX_NOTE_LENGTH = 200

    /** A venue must be chosen (or typed): the name cannot be empty or only spaces. */
    fun validateVenue(venueName: String): ValidationResult =
        if (venueName.isBlank()) ValidationResult.Error(R.string.error_venue_required)
        else ValidationResult.Success

    /** Rating must be 1 to 5 stars. 0 means the user has not picked one yet. */
    fun validateRating(rating: Int): ValidationResult =
        if (rating in MIN_RATING..MAX_RATING) ValidationResult.Success
        else ValidationResult.Error(R.string.error_rating_range)

    /** The note is optional, but it can be at most 200 characters. */
    fun validateNote(note: String): ValidationResult =
        if (note.length <= MAX_NOTE_LENGTH) ValidationResult.Success
        else ValidationResult.Error(R.string.error_note_too_long)
}