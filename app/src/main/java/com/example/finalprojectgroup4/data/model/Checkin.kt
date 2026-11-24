package com.example.finalprojectgroup4.data.model

/**
 * One saved check-in (matches the SQLite checkins table and the Firestore document).
 *
 * Kind: data class. Owner: Track B (Ana).
 */
data class Checkin(
    val id: String
    // TODO: userId, venueId, venueName, address, latitude, longitude,
    //       category, rating, note, createdAt, updatedAt, synced, deleted
)
