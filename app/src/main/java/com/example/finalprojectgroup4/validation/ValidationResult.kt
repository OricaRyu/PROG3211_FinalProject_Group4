package com.example.finalprojectgroup4.validation

import androidx.annotation.StringRes

/**
 * The result of checking one input field: Success, or Error with the message to show.
 *
 * @property messageRes the strings.xml message, for example R.string.error_note_too_long
 * @property formatArgs values for the message's placeholders (%1$d, %2$d...), in order.
 *   Empty for messages without placeholders.
 *
 * In a composable, show an error with:
 *   stringResource(error.messageRes, *error.formatArgs.toTypedArray())
 *
 * Kind: sealed class. Owner: Track B (Ana).
 */
sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(
        @param:StringRes val messageRes: Int,
        val formatArgs: List<Any> = emptyList()
    ) : ValidationResult()
}