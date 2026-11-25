package com.example.finalprojectgroup4.features.checkins

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojectgroup4.data.model.Checkin
import com.example.finalprojectgroup4.data.sample.SampleCheckinStore
import com.example.finalprojectgroup4.navigation.Routes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Loads one check-in for the Detail screen and deletes it.
 *
 * The check-in id comes from the route "checkin/{id}": Navigation puts route arguments
 * into the SavedStateHandle automatically, so the screen doesn't have to pass it in.
 *
 * Kind: class extending ViewModel. Owner: Track A (Sébastien).
 */
class CheckinDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val checkinId: String = savedStateHandle[Routes.ARG_ID] ?: ""

    // Set when the user confirms Delete, so the screen keeps showing the check-in
    // while it navigates away instead of flashing "not found"
    private var isDeleting = false
    private var lastLoaded: Checkin? = null

    // TODO (M3): read from CheckinRepository (Track B) instead of SampleCheckinStore
    val uiState: StateFlow<CheckinDetailUiState> =
        SampleCheckinStore.observe(checkinId)
            .map { checkin ->
                when {
                    checkin != null -> {
                        lastLoaded = checkin
                        CheckinDetailUiState.Loaded(checkin)
                    }
                    isDeleting && lastLoaded != null -> CheckinDetailUiState.Loaded(lastLoaded!!)
                    else -> CheckinDetailUiState.NotFound
                }
            }
            // Keeps the latest state while the screen is visible (and 5 s after, for rotation)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CheckinDetailUiState.Loading
            )

    /** Deletes this check-in. Called only after the user confirms in the dialog. */
    fun delete() {
        isDeleting = true
        // TODO (M3): CheckinRepository.delete (marks it deleted, then syncs to Firestore)
        SampleCheckinStore.delete(checkinId)
    }
}
