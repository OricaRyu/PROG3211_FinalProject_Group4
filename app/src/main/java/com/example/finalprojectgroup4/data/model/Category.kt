package com.example.finalprojectgroup4.data.model

/**
 * The three categories a user can pick for a check-in.
 * Room and Firestore store it as the enum name ("RESTAURANT", "EVENT", "OTHER").
 *
 * Kind: enum class. Owner: Track B (Ana).
 */
enum class Category {
    RESTAURANT,
    EVENT,
    OTHER;

    companion object {
        /**
         * Turns a stored text value back into a Category (for example from Firestore, Track C).
         * Unknown or missing values fall back to OTHER instead of crashing.
         */
        fun fromStored(value: String?): Category =
            entries.firstOrNull { it.name == value } ?: OTHER
    }
}