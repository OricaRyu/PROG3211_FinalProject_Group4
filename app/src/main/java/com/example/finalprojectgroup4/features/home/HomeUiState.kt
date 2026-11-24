package com.example.finalprojectgroup4.features.home

import com.example.finalprojectgroup4.data.model.Category

/**
 * Everything Home needs to draw itself. HomeViewModel creates it; HomeScreen only reads it.
 *
 * Kind: data class. Owner: Track A (Sébastien).
 */
data class HomeUiState(
    val nearbyVenues: List<NearbyVenueItem> = emptyList(),
    val recentCheckins: List<RecentCheckinItem> = emptyList(),
    val isSynced: Boolean = false
)

/**
 * One venue in the "Nearby now" section.
 * Later this is built from a Geoapify Venue (Track D).
 *
 * Kind: data class. Owner: Track A (Sébastien).
 */
data class NearbyVenueItem(
    val id: String,
    val name: String,
    val category: Category,
    val distanceMeters: Int
)

/**
 * One check-in in the "Latest check-ins" section.
 * Later this is built from a saved Checkin (Track B).
 *
 * Kind: data class. Owner: Track A (Sébastien).
 */
data class RecentCheckinItem(
    val id: String,
    val venueName: String,
    val category: Category,
    val note: String,
    val createdAt: Long // epoch millis
)
