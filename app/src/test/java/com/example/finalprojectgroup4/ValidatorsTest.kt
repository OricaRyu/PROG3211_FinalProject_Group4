package com.example.finalprojectgroup4

import com.example.finalprojectgroup4.validation.ValidationResult
import com.example.finalprojectgroup4.validation.Validators
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for the check-in validators. They run on the computer, no emulator needed.
 *
 * Kind: unit test class. Owner: Track B (Ana).
 */
class ValidatorsTest {

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
    fun ratingOutsideOneToFiveIsError() {
        assertEquals(ValidationResult.Error(R.string.error_rating_range), Validators.validateRating(0))
        assertEquals(ValidationResult.Error(R.string.error_rating_range), Validators.validateRating(6))
        assertEquals(ValidationResult.Error(R.string.error_rating_range), Validators.validateRating(-1))
    }

    @Test
    fun ratingOneToFiveIsValid() {
        for (rating in 1..5) {
            assertEquals(ValidationResult.Success, Validators.validateRating(rating))
        }
    }

    @Test
    fun noteUpTo200CharactersIsValid() {
        assertEquals(ValidationResult.Success, Validators.validateNote(""))           // optional
        assertEquals(ValidationResult.Success, Validators.validateNote("a".repeat(200))) // exactly the limit
    }

    @Test
    fun noteOver200CharactersIsError() {
        assertEquals(ValidationResult.Error(R.string.error_note_too_long), Validators.validateNote("a".repeat(201)))
    }
}