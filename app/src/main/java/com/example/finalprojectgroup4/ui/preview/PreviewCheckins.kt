package com.example.finalprojectgroup4.ui.preview

import com.example.finalprojectgroup4.data.model.Category
import com.example.finalprojectgroup4.data.model.Checkin

/**
 * FOR @Preview FUNCTIONS ONLY. A few fixed check-ins matching the Figma mockup, so screens
 * can be previewed without a database. The app itself always reads from CheckinRepository.
 *
 * The names and notes are fake records, not UI text, so they live here instead of strings.xml.
 *
 * Kind: top-level val. Owner: Track B (Ana).
 */
val previewCheckins: List<Checkin> = listOf(
    Checkin(
        id = "c1", userId = "preview-user", venueName = "Maple Bistro",
        address = "123 Queen St, Waterloo", latitude = 43.4643, longitude = -80.5204,
        category = Category.RESTAURANT, rating = 4,
        note = "Great pasta, will come back with the team.",
        createdAt = 1794184200000L, // Nov 8, 2026, 7:30 PM
        updatedAt = 1794233700000L, // edited Nov 9, 2026, 9:15 AM
        synced = true
    ),
    Checkin(
        id = "c2", userId = "preview-user", venueName = "Jazz Night",
        address = "59 King St N, Waterloo", latitude = 43.4668, longitude = -80.5228,
        category = Category.EVENT, rating = 4, note = "Loud but fun",
        createdAt = 1793930400000L, updatedAt = 1793930400000L, // Nov 5, 2026, 9:00 PM
        synced = true
    ),
    Checkin(
        id = "c3", userId = "preview-user", venueName = "Blue Door Cafe",
        address = null, // address is optional
        latitude = 43.4655, longitude = -80.5230,
        category = Category.RESTAURANT, rating = 4, note = "Flat white",
        createdAt = 1793362800000L, updatedAt = 1793362800000L, // Oct 30, 2026, 8:20 AM
        synced = false // shows the "Not synced" state
    )
)