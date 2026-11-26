package com.example.finalprojectgroup4.features.checkins

import android.app.Application
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
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = CheckinRepository.getInstance(application)

    // null on New check-in, the check-in's id on Edit
    private val checkinId: String? = savedStateHandle[Routes.ARG_ID]

    // The check-in being edited, kept so the fields the form doesn't show
    // (venue id, map position, created date) are saved unchanged
    private var original: Checkin? = null

    private val _uiState = MutableStateFlow(
        CheckinUiState(isEditing = checkinId != null, isLoading = checkinId != null)
    )
    val uiState: StateFlow<CheckinUiState> = _uiState.asStateFlow()

    private val _isSaved = MutableStateFlow(false)

    /** Becomes true once Room has confirmed the save. The screen closes when it sees it. */
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    init {
        if (checkinId != null) load(checkinId)
    }

    /** Edit only: reads the check-in once and fills the form with it. */
    private fun load(id: String) {
        viewModelScope.launch {
            val checkin = runCatching { repository.getById(id) }.getOrNull()
            original = checkin
            _uiState.update { state ->
                if (checkin == null) {
                    state.copy(isLoading = false, notFound = true)
                } else {
                    state.copy(
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

    // ---------- Field changes (each one clears that field's old error) ----------

    fun onVenueNameChange(value: String) =
        _uiState.update { it.copy(venueName = value, venueError = null) }

    fun onAddressChange(value: String) =
        _uiState.update { it.copy(address = value) }

    fun onCategoryChange(value: Category) =
        _uiState.update { it.copy(category = value) }

    fun onRatingChange(value: Int) =
        _uiState.update { it.copy(rating = value, ratingError = null) }

    fun onNoteChange(value: String) =
        // Checked while typing, so the error shows as soon as the note passes the limit
        _uiState.update { it.copy(note = value, noteError = Validators.validateNote(value).errorOrNull()) }

    // ---------- Save ----------

    /** Validates every field; saves only when all of them pass. */
    fun save() {
        val form = _uiState.value
        // Ignore taps while loading, while a save runs, or after it finished
        if (form.isLoading || form.notFound || form.isSaving || _isSaved.value) return

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
            // runCatching: a database error counts as "not saved" instead of crashing.
            val ok = runCatching {
                withContext(NonCancellable) { write(form) }
            }.getOrDefault(false)

            if (ok) _isSaved.value = true // close only once Room confirms
            else _uiState.update { it.copy(isSaving = false, saveFailed = true) }
        }
    }

    /** Creates or updates the check-in. Empty optional fields are saved as null. */
    private suspend fun write(form: CheckinUiState): Boolean {
        val venueName = form.venueName.trim()
        val address = form.address.trim().ifEmpty { null }
        val note = form.note.trim().ifEmpty { null }
        val editing = original

        return if (editing == null) {
            repository.create(
                venueName = venueName,
                category = form.category,
                rating = form.rating,
                note = note,
                address = address
            )
            true
        } else {
            repository.update(
                editing.copy(
                    venueName = venueName,
                    address = address,
                    category = form.category,
                    rating = form.rating,
                    note = note
                )
            )
        }
    }

    /** Success -> null, Error -> the error itself. */
    private fun ValidationResult.errorOrNull(): ValidationResult.Error? = this as? ValidationResult.Error
}