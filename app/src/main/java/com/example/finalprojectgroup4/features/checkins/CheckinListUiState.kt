package com.example.finalprojectgroup4.features.checkins

import com.example.finalprojectgroup4.data.model.Category
import com.example.finalprojectgroup4.data.model.Checkin

/**
 * Everything the Check-ins list needs to draw itself. CheckinListViewModel creates it;
 * the screen only reads it.
 *
 * @property checkins the check-ins to show, already filtered by the selected category
 * @property selectedCategory the chosen filter chip, or null for "All"
 * @property hasAnyCheckins false only when the user has no check-ins at all,
 *   so the screen can tell "nothing yet" apart from "nothing in this category"
 * @property isLoading true until the first list arrives; the screen shows a spinner
 *   instead of an empty message so it never says "No check-ins yet" by mistake
 *
 * Kind: data class. Owner: Track A (Sébastien).
 */
data class CheckinListUiState(
    val checkins: List<Checkin> = emptyList(),
    val selectedCategory: Category? = null,
    val hasAnyCheckins: Boolean = false,
    val isLoading: Boolean = false
)
