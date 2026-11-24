package com.example.finalprojectgroup4.features.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Holds Home's state and survives screen rotation.
 * For now it starts with sample data; the screen does not know or care where the data came from.
 *
 * Kind: class extending ViewModel. Owner: Track A (Sébastien), with Track D.
 */
class HomeViewModel : ViewModel() {

    // Private and writable inside the ViewModel; the screen only gets the read-only version
    private val _uiState = MutableStateFlow(
        HomeUiState(
            // TODO (M3): load from VenueRepository (Track D) and CheckinRepository (Track B)
            nearbyVenues = HomeSampleData.nearbyVenues,
            recentCheckins = HomeSampleData.recentCheckins,
            // TODO (M3): real sync status from Firestore (Track C)
            isSynced = true
        )
    )

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}
