package com.example.finalprojectgroup4.features.home

import com.example.finalprojectgroup4.data.model.Category

/**
 * Temporary sample data for Home, matching the Figma mockup.
 * These names and notes are fake records, not UI text, so they live here instead of strings.xml.
 *
 * To replace: HomeViewModel loads real data from VenueRepository (Track D) and
 * CheckinRepository (Track B); then delete this file.
 *
 * Kind: object. Owner: Track A (Sébastien).
 */
object HomeSampleData {

    val nearbyVenues = listOf(
        NearbyVenueItem(id = "v1", name = "Blue Door Cafe", category = Category.RESTAURANT, distanceMeters = 120),
        NearbyVenueItem(id = "v2", name = "Riverside Hall", category = Category.EVENT, distanceMeters = 340)
    )

    val recentCheckins = listOf(
        RecentCheckinItem(
            id = "c1",
            venueName = "Maple Bistro",
            category = Category.RESTAURANT,
            note = "Great pasta",
            createdAt = 1794157200000L // Nov 8, 2026
        ),
        RecentCheckinItem(
            id = "c2",
            venueName = "Jazz Night",
            category = Category.EVENT,
            note = "Loud but fun",
            createdAt = 1793898000000L // Nov 5, 2026
        )
    )
}
