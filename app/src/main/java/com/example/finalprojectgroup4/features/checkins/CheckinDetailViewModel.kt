package com.example.finalprojectgroup4.features.checkins

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojectgroup4.data.model.Checkin
import com.example.finalprojectgroup4.data.sample.SampleCheckinStore
import com.example.finalprojectgroup4.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

    private val _isDeleted = MutableStateFlow(false)

    /**
     * Becomes true once the user confirms Delete. The screen navigates back when it sees it,
     * exactly once, however many times the buttons are tapped while the screen is leaving.
     */
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()

    // The last check-in shown, so the screen keeps showing it while it navigates away
    // after Delete instead of flashing "not found"
    private var lastLoaded: Checkin? = null

    // TODO (M3): read from CheckinRepository (Track B) instead of SampleCheckinStore
    val uiState: StateFlow<CheckinDetailUiState> =
        combine(SampleCheckinStore.observe(checkinId), _isDeleted) { checkin, isDeleted ->
            // A soft-deleted check-in (deleted = true, waiting to sync) counts as gone
            val visible = checkin?.takeIf { !it.deleted }
            val shown = lastLoaded
            when {
                visible != null && !isDeleted -> {
                    lastLoaded = visible
                    CheckinDetailUiState.Loaded(visible)
                }
                isDeleted && shown != null -> CheckinDetailUiState.Loaded(shown)
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
        if (_isDeleted.value) return // already deleted; ignore repeat taps
        _isDeleted.value = true
        // TODO (M3): CheckinRepository.delete (marks it deleted, then syncs to Firestore)
        SampleCheckinStore.delete(checkinId)
    }
}
