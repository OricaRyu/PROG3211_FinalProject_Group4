package com.example.finalprojectgroup4.validation

/**
 * The result of checking one input field.
 *
 * Kind: sealed class. Owner: Track B (Ana).
 */
sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val messageRes: Int) : ValidationResult()
}
