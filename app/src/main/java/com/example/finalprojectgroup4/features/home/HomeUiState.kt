package com.example.finalprojectgroup4.features.home

/**
 * Everything Home needs to draw itself (nearby venues, recent check-ins, loading, error).
 *
 * Kind: data class. Owner: Track A (Sébastien).
 */
data class HomeUiState(
    val isLoading: Boolean = false
    // TODO: nearbyVenues, recentCheckins, errorMessage
)
