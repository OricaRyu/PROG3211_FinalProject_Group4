package com.example.finalprojectgroup4.features.checkins

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojectgroup4.data.model.Category
import com.example.finalprojectgroup4.data.sample.SampleCheckinStore
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
 * Kind: class extending ViewModel. Owner: Track A (Sébastien).
 */
class CheckinListViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    // null means "All"
    private val selectedCategory: StateFlow<Category?> =
        savedStateHandle.getStateFlow(KEY_CATEGORY, null)

    // TODO (M3): read from CheckinRepository (Track B) instead of SampleCheckinStore.
    //  The repository's query must sort by createdAt, newest first: this ViewModel
    //  shows whatever order it receives.
    val uiState: StateFlow<CheckinListUiState> =
        combine(SampleCheckinStore.checkins, selectedCategory) { checkins, category ->
            // A soft-deleted check-in (deleted = true, waiting to sync) counts as gone,
            // the same way the Detail screen treats it
            val all = checkins.filterNot { it.deleted }
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
