package com.example.finalprojectgroup4.features.checkins

import com.example.finalprojectgroup4.data.model.Checkin

/**
 * What the Detail screen can be showing. A sealed interface means the screen's `when`
 * must handle all three cases, so none can be forgotten.
 *
 * Kind: sealed interface. Owner: Track A (Sébastien).
 */
sealed interface CheckinDetailUiState {

    /** Still looking the check-in up. */
    data object Loading : CheckinDetailUiState

    /** No check-in with this id (for example, it was deleted). */
    data object NotFound : CheckinDetailUiState

    /** The check-in to show. */
    data class Loaded(val checkin: Checkin) : CheckinDetailUiState
}
