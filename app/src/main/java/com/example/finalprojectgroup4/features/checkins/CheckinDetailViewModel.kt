package com.example.finalprojectgroup4.features.checkins

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.finalprojectgroup4.data.model.Checkin
import com.example.finalprojectgroup4.data.repository.CheckinRepository
import com.example.finalprojectgroup4.navigation.Routes
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    // Set as soon as Delete is confirmed: keeps the check-in on screen and blocks repeat taps
    private val _isDeleting = MutableStateFlow(false)

    // Set only after Room confirms the delete
    private val _isDeleted = MutableStateFlow(false)

    /**
     * Becomes true once Room has confirmed the delete. The screen navigates back when it sees it,
     * exactly once, however many times the buttons are tapped while the screen is leaving.
     */
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()

    // The last check-in shown, so the screen keeps showing it while the delete runs
    // and while it navigates away, instead of flashing "not found"
    private var lastLoaded: Checkin? = null

    val uiState: StateFlow<CheckinDetailUiState> =
        combine(repository.observe(checkinId), _isDeleting) { checkin, isDeleting ->
            // The repository already returns null for a deleted check-in
            val shown = lastLoaded
            when {
                checkin != null && !isDeleting -> {
                    lastLoaded = checkin
                    CheckinDetailUiState.Loaded(checkin)
                }
                isDeleting && shown != null -> CheckinDetailUiState.Loaded(shown)
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
        if (_isDeleting.value) return // already deleting; ignore repeat taps
        _isDeleting.value = true
        viewModelScope.launch {
            // NonCancellable: leaving the screen can't cut the write off halfway.
            // runCatching: a database error counts as "not deleted" instead of crashing.
            val ok = runCatching {
                withContext(NonCancellable) { repository.delete(checkinId) }
            }.getOrDefault(false)
            if (ok) _isDeleted.value = true // navigate back only once Room confirms
            else _isDeleting.value = false  // stay on the screen so the user can retry
        }
    }
}