package com.example.finalprojectgroup4.features.checkins

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.finalprojectgroup4.data.model.Checkin
import com.example.finalprojectgroup4.data.repository.CheckinRepository
import com.example.finalprojectgroup4.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Loads one check-in for the Detail screen and deletes it.
 *
 * The check-in id comes from the route "checkin/{id}": Navigation puts route arguments
 * into the SavedStateHandle automatically, so the screen doesn't have to pass it in.
 *
 * It is an AndroidViewModel because CheckinRepository needs the app's Context to open the database.
 *
 * Kind: class extending AndroidViewModel. Owner: Track A (Sébastien), data from Track B.
 */
class CheckinDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = CheckinRepository.getInstance(application)

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

    val uiState: StateFlow<CheckinDetailUiState> =
        combine(repository.observe(checkinId), _isDeleted) { checkin, isDeleted ->
            // The repository already returns null for a deleted check-in
            val shown = lastLoaded
            when {
                checkin != null && !isDeleted -> {
                    lastLoaded = checkin
                    CheckinDetailUiState.Loaded(checkin)
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
        // Soft delete in Room; Track C's sync sends it to Firestore later
        viewModelScope.launch { repository.delete(checkinId) }
    }
}