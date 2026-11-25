package com.example.finalprojectgroup4.features.checkins

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.finalprojectgroup4.data.model.Category
import com.example.finalprojectgroup4.data.repository.CheckinRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Holds the Check-ins list and the selected category filter.
 *
 * Filtering happens here, not in the screen: whenever the check-ins or the selected chip
 * change, `combine` builds a new CheckinListUiState and the screen redraws.
 *
 * The selected chip lives in the SavedStateHandle, so it survives the app being killed
 * in the background (a plain MutableStateFlow would reset to "All").
 *
 * It is an AndroidViewModel because CheckinRepository needs the app's Context to open the database.
 *
 * Kind: class extending AndroidViewModel. Owner: Track A (Sébastien), data from Track B.
 */
class CheckinListViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = CheckinRepository.getInstance(application)

    // null means "All"
    private val selectedCategory: StateFlow<Category?> =
        savedStateHandle.getStateFlow(KEY_CATEGORY, null)

    // The repository already leaves out deleted check-ins and sorts newest first
    val uiState: StateFlow<CheckinListUiState> =
        combine(repository.checkins, selectedCategory) { all, category ->
            CheckinListUiState(
                checkins = if (category == null) all else all.filter { it.category == category },
                selectedCategory = category,
                hasAnyCheckins = all.isNotEmpty(),
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            // Loading until the first list arrives, so "No check-ins yet" doesn't flash
            // (with the saved chip, so the right one shows selected while loading)
            initialValue = CheckinListUiState(
                selectedCategory = selectedCategory.value,
                isLoading = true
            )
        )

    /** Called when a filter chip is tapped. null selects "All". */
    fun selectCategory(category: Category?) {
        savedStateHandle[KEY_CATEGORY] = category
    }

    private companion object {
        const val KEY_CATEGORY = "selectedCategory"
    }
}