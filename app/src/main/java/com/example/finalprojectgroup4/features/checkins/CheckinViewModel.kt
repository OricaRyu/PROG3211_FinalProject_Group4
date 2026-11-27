package com.example.finalprojectgroup4.features.checkins

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.finalprojectgroup4.data.model.Category
import com.example.finalprojectgroup4.data.model.Checkin
import com.example.finalprojectgroup4.data.repository.CheckinRepository
import com.example.finalprojectgroup4.navigation.Routes
import com.example.finalprojectgroup4.validation.ValidationResult
import com.example.finalprojectgroup4.validation.Validators
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * State and logic for the New / Edit check-in form.
 *
 * New or Edit comes from the route: "edit/{id}" puts the id in the SavedStateHandle,
 * "new" has none. On Edit, the check-in is loaded once and its fields fill the form.
 *
 * It is an AndroidViewModel because CheckinRepository needs the app's Context to open the database.
 *
 * Kind: class extending AndroidViewModel. Owner: Track B (Ana).
 */
class CheckinViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = CheckinRepository.getInstance(application)

    // null on New check-in, the check-in's id on Edit
    private val checkinId: String? = savedStateHandle[Routes.ARG_ID]

    // The check-in being edited, kept so the fields the form doesn't show
    // (venue id, map position, created date) are saved unchanged
    private var original: Checkin? = null

    // true when the user had already typed something before Android killed the app
    private val hasDraft: Boolean = savedStateHandle[KEY_HAS_DRAFT] ?: false

    private val _uiState = MutableStateFlow(
        restoreDraft(CheckinUiState(isEditing = checkinId != null, isLoading = checkinId != null))
    )
    val uiState: StateFlow<CheckinUiState> = _uiState.asStateFlow()

    private val _isSaved = MutableStateFlow(false)

    /** Becomes true once Room has confirmed the save. The screen closes when it sees it. */
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    /** What a save attempt ended with (a database error is caught separately). */
    private enum class SaveResult { SAVED, GONE }

    init {
        if (checkinId != null) load(checkinId)
    }

    // ---------- Loading (Edit only) ----------

    /**
     * Reads the check-in once. A database error shows Retry; a missing check-in shows
     * "no longer exists". If a draft was restored, the user's unsaved changes are kept
     * instead of being replaced by the database copy.
     */
    private fun load(id: String) {
        viewModelScope.launch {
            val result = runCatching { repository.getById(id) }
            result.exceptionOrNull()?.let { e ->
                Log.e(TAG, "Loading check-in failed", e)
                _uiState.update { it.copy(isLoading = false, loadFailed = true) }
                return@launch
            }

            val checkin = result.getOrNull()
            original = checkin
            _uiState.update { state ->
                when {
                    checkin == null -> state.copy(isLoading = false, notFound = true)
                    hasDraft -> state.copy(isLoading = false) // keep the restored draft
                    else -> state.copy(
                        isLoading = false,
                        venueName = checkin.venueName,
                        address = checkin.address.orEmpty(),
                        category = checkin.category,
                        rating = checkin.rating,
                        note = checkin.note.orEmpty()
                    )
                }
            }
        }
    }

    /** Retry button after a load error. */
    fun retryLoad() {
        val id = checkinId ?: return
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, loadFailed = false) }
        load(id)
    }

    // ---------- Field changes ----------
    // Each one clears that field's old error and the "Couldn't save" message,
    // so it doesn't look like the new input is the problem, then saves the draft.

    fun onVenueNameChange(value: String) =
        change { it.copy(venueName = value, venueError = null) }

    fun onAddressChange(value: String) =
        change { it.copy(address = value) }

    fun onCategoryChange(value: Category) =
        change { it.copy(category = value) }

    fun onRatingChange(value: Int) =
        change { it.copy(rating = value, ratingError = null) }

    fun onNoteChange(value: String) =
        // Checked while typing, so the error shows as soon as the note passes the limit
        change { it.copy(note = value, noteError = Validators.validateNote(value).errorOrNull()) }

    /** Applies a field change, clears saveFailed, and keeps the draft in the SavedStateHandle. */
    private fun change(edit: (CheckinUiState) -> CheckinUiState) {
        _uiState.update { edit(it).copy(saveFailed = false) }
        saveDraft(_uiState.value)
    }

    // ---------- Save ----------

    /** Validates every field; saves only when all of them pass. */
    fun save() {
        val form = _uiState.value
        // Ignore taps while loading, while a save runs, or after it finished
        if (form.isLoading || form.loadFailed || form.notFound || form.isSaving || _isSaved.value) return

        val venueError = Validators.validateVenue(form.venueName).errorOrNull()
        val ratingError = Validators.validateRating(form.rating).errorOrNull()
        val noteError = Validators.validateNote(form.note).errorOrNull()

        if (venueError != null || ratingError != null || noteError != null) {
            _uiState.update {
                it.copy(venueError = venueError, ratingError = ratingError, noteError = noteError)
            }
            return
        }

        _uiState.update { it.copy(isSaving = true, saveFailed = false) }
        viewModelScope.launch {
            // NonCancellable: closing the screen can't cut the write off halfway.
            // A database error is logged and counts as "not saved" instead of crashing.
            val result = runCatching { withContext(NonCancellable) { write(form) } }
                .onFailure { e -> Log.e(TAG, "Saving check-in failed", e) }
                .getOrNull()

            when (result) {
                SaveResult.SAVED -> _isSaved.value = true // close only once Room confirms
                // Not an error: the check-in was deleted (or isn't this user's) while the form was open
                SaveResult.GONE -> _uiState.update { it.copy(isSaving = false, notFound = true) }
                null -> _uiState.update { it.copy(isSaving = false, saveFailed = true) }
            }
        }
    }

    /** Creates or updates the check-in. Empty optional fields are saved as null. */
    private suspend fun write(form: CheckinUiState): SaveResult {
        val venueName = form.venueName.trim()
        val address = form.address.trim().ifEmpty { null }
        val note = form.note.trim().ifEmpty { null }
        val editing = original

        if (editing == null) {
            repository.create(
                venueName = venueName,
                category = form.category,
                rating = form.rating,
                note = note,
                address = address
            )
            return SaveResult.SAVED
        }

        // update() returns false on purpose when the row is missing, deleted or not this user's
        val changed = repository.update(
            editing.copy(
                venueName = venueName,
                address = address,
                category = form.category,
                rating = form.rating,
                note = note
            )
        )
        return if (changed) SaveResult.SAVED else SaveResult.GONE
    }

    // ---------- Draft (survives Android killing the app) ----------

    private fun saveDraft(state: CheckinUiState) {
        savedStateHandle[KEY_HAS_DRAFT] = true
        savedStateHandle[KEY_VENUE] = state.venueName
        savedStateHandle[KEY_ADDRESS] = state.address
        savedStateHandle[KEY_CATEGORY] = state.category.name
        savedStateHandle[KEY_RATING] = state.rating
        savedStateHandle[KEY_NOTE] = state.note
    }

    /** Puts a saved draft's fields into [base], or returns [base] unchanged when there is none. */
    private fun restoreDraft(base: CheckinUiState): CheckinUiState {
        if (!hasDraft) return base
        val note: String = savedStateHandle[KEY_NOTE] ?: ""
        return base.copy(
            venueName = savedStateHandle[KEY_VENUE] ?: "",
            address = savedStateHandle[KEY_ADDRESS] ?: "",
            category = Category.fromStored(savedStateHandle[KEY_CATEGORY]),
            rating = savedStateHandle[KEY_RATING] ?: 0,
            note = note,
            noteError = Validators.validateNote(note).errorOrNull()
        )
    }

    /** Success -> null, Error -> the error itself. */
    private fun ValidationResult.errorOrNull(): ValidationResult.Error? = this as? ValidationResult.Error

    private companion object {
        const val TAG = "CheckinViewModel"
        const val KEY_HAS_DRAFT = "draft"
        const val KEY_VENUE = "draftVenueName"
        const val KEY_ADDRESS = "draftAddress"
        const val KEY_CATEGORY = "draftCategory"
        const val KEY_RATING = "draftRating"
        const val KEY_NOTE = "draftNote"
    }
}