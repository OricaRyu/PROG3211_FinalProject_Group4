package com.example.finalprojectgroup4.validation

import com.example.finalprojectgroup4.R

/**
 * Validation rules for the check-in form. Each function checks one field and returns
 * ValidationResult.Success, or ValidationResult.Error with a strings.xml message.
 *
 * The limits below are the single source for the whole app: the error messages, the form's
 * note counter and the RatingStars component all read them, so a limit only changes here.
 *
 * The form (#23) runs all of them when Save is tapped and shows each error under its field.
 * Login rules (email format, password not empty) are Track C's (#25) and go in this object too.
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

    /** Rating must be MIN_RATING to MAX_RATING stars. 0 means the user has not picked one yet. */
    fun validateRating(rating: Int): ValidationResult =
        if (rating in MIN_RATING..MAX_RATING) ValidationResult.Success
        else ValidationResult.Error(R.string.error_rating_range, listOf(MIN_RATING, MAX_RATING))

    /**
     * The note is optional (null or empty is fine, like Checkin.note),
     * but it can be at most MAX_NOTE_LENGTH characters.
     */
    fun validateNote(note: String?): ValidationResult =
        if ((note?.length ?: 0) <= MAX_NOTE_LENGTH) ValidationResult.Success
        else ValidationResult.Error(R.string.error_note_too_long, listOf(MAX_NOTE_LENGTH))
}