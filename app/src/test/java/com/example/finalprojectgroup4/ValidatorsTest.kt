package com.example.finalprojectgroup4

import com.example.finalprojectgroup4.validation.ValidationResult
import com.example.finalprojectgroup4.validation.Validators
import com.example.finalprojectgroup4.validation.Validators.MAX_NOTE_LENGTH
import com.example.finalprojectgroup4.validation.Validators.MAX_RATING
import com.example.finalprojectgroup4.validation.Validators.MIN_RATING
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for the check-in validators. They run on the computer, no emulator needed.
 * They use the Validators constants, so they still pass if a limit changes.
 *
 * Kind: unit test class. Owner: Track B (Ana).
 */
class ValidatorsTest {

    private val ratingError = ValidationResult.Error(R.string.error_rating_range, listOf(MIN_RATING, MAX_RATING))
    private val noteError = ValidationResult.Error(R.string.error_note_too_long, listOf(MAX_NOTE_LENGTH))

    @Test
    fun venueBlankIsError() {
        assertEquals(ValidationResult.Error(R.string.error_venue_required), Validators.validateVenue(""))
        assertEquals(ValidationResult.Error(R.string.error_venue_required), Validators.validateVenue("   "))
    }

    @Test
    fun venueWithNameIsValid() {
        assertEquals(ValidationResult.Success, Validators.validateVenue("Maple Bistro"))
    }

    @Test
    fun ratingOutsideLimitsIsError() {
        assertEquals(ratingError, Validators.validateRating(MIN_RATING - 1)) // 0 = not picked yet
        assertEquals(ratingError, Validators.validateRating(MAX_RATING + 1))
        assertEquals(ratingError, Validators.validateRating(-1))
    }

    @Test
    fun ratingWithinLimitsIsValid() {
        for (rating in MIN_RATING..MAX_RATING) {
            assertEquals(ValidationResult.Success, Validators.validateRating(rating))
        }
    }

    @Test
    fun noteMissingOrEmptyIsValid() {
        assertEquals(ValidationResult.Success, Validators.validateNote(null)) // optional
        assertEquals(ValidationResult.Success, Validators.validateNote(""))
    }

    @Test
    fun noteUpToLimitIsValid() {
        assertEquals(ValidationResult.Success, Validators.validateNote("a".repeat(MAX_NOTE_LENGTH)))
    }

    @Test
    fun noteOverLimitIsError() {
        assertEquals(noteError, Validators.validateNote("a".repeat(MAX_NOTE_LENGTH + 1)))
    }

    @Test
    fun errorsCarryTheLimitsForTheMessage() {
        // The message placeholders get the real limits, not numbers typed into strings.xml
        assertEquals(listOf(MIN_RATING, MAX_RATING), (Validators.validateRating(0) as ValidationResult.Error).formatArgs)
        assertEquals(listOf(MAX_NOTE_LENGTH), (Validators.validateNote("a".repeat(MAX_NOTE_LENGTH + 1)) as ValidationResult.Error).formatArgs)
    }
}