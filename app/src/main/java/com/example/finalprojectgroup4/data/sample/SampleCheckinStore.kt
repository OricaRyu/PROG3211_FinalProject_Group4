package com.example.finalprojectgroup4.data.sample

import com.example.finalprojectgroup4.data.model.Category
import com.example.finalprojectgroup4.data.model.Checkin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * TEMPORARY in-memory check-ins, matching the Figma mockup, so the Detail and List screens
 * can be built and demoed before the real repository exists.
 *
 * It uses the real Checkin model (Track B) and offers the same kind of functions the
 * repository will (observe all, observe one, delete), so switching over later only means
 * changing which object the ViewModels call. Data resets when the app restarts.
 *
 * To replace: CheckinRepository (Track B), then delete this file.
 * The names and notes below are fake records, not UI text, so they live here instead of strings.xml.
 *
 * Kind: object. Owner: Track A (Sébastien).
 */
object SampleCheckinStore {

    private const val SAMPLE_USER = "local-user"

    /**
     * FOR @Preview FUNCTIONS ONLY. The starting check-ins as a fixed list: it never changes,
     * so deletes don't show up in it. Screens and ViewModels must use [checkins] instead.
     *
     * Previews can't use `checkins.value`: reading a StateFlow's value inside a composable
     * doesn't subscribe to it, so lint flags it.
     */
    val previewCheckins: List<Checkin> =
        listOf(
            Checkin(
                id = "c1", userId = SAMPLE_USER, venueName = "Maple Bistro",
                address = "123 Queen St, Waterloo", latitude = 43.4643, longitude = -80.5204,
                category = Category.RESTAURANT, rating = 4,
                note = "Great pasta, will come back with the team.",
                createdAt = 1794184200000L, // Nov 8, 2026, 7:30 PM
                updatedAt = 1794233700000L, // edited Nov 9, 2026, 9:15 AM
                synced = true
            ),
            Checkin(
                id = "c2", userId = SAMPLE_USER, venueName = "Jazz Night",
                address = "59 King St N, Waterloo", latitude = 43.4668, longitude = -80.5228,
                category = Category.EVENT, rating = 4, note = "Loud but fun",
                createdAt = 1793930400000L, updatedAt = 1793930400000L, // Nov 5, 2026, 9:00 PM
                synced = true
            ),
            Checkin(
                id = "c3", userId = SAMPLE_USER, venueName = "Harbour Park",
                address = null, // address is optional
                latitude = 43.4500, longitude = -80.4920,
                category = Category.OTHER, rating = 5, note = "Sunset walk",
                createdAt = 1793659500000L, updatedAt = 1793659500000L, // Nov 2, 2026, 5:45 PM
                synced = true
            ),
            Checkin(
                id = "c4", userId = SAMPLE_USER, venueName = "Blue Door Cafe",
                address = "88 King St, Waterloo", latitude = 43.4655, longitude = -80.5230,
                category = Category.RESTAURANT, rating = 4, note = "Flat white",
                createdAt = 1793362800000L, updatedAt = 1793362800000L, // Oct 30, 2026, 8:20 AM
                synced = false // shows the "Not synced" state
            ),
            Checkin(
                id = "c5", userId = SAMPLE_USER, venueName = "Tech Meetup",
                address = "Communitech, Waterloo", latitude = 43.4515, longitude = -80.4986,
                category = Category.EVENT, rating = 3, note = "Good talks",
                createdAt = 1793140200000L, updatedAt = 1793140200000L, // Oct 27, 2026, 6:30 PM
                synced = true
            )
        )

    private val _checkins = MutableStateFlow(previewCheckins)

    /**
     * All check-ins, in the order of the list above (written newest first). Updates whenever
     * one changes. delete() removes rows outright, so none here are ever marked deleted.
     * The real repository must do its own filtering and sorting.
     */
    val checkins: StateFlow<List<Checkin>> = _checkins.asStateFlow()

    /** One check-in by id, or null when it doesn't exist (or was removed). */
    fun observe(id: String): Flow<Checkin?> =
        _checkins.map { list -> list.firstOrNull { it.id == id } }

    /** Removes a check-in. Everything observing the store updates automatically. */
    fun delete(id: String) {
        _checkins.update { list -> list.filterNot { it.id == id } }
    }
}
