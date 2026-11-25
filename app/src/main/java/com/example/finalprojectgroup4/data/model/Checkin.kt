package com.example.finalprojectgroup4.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One saved check-in (matches the SQLite checkins table and the Firestore document).
 *
 * @property id UUID string. Also used as the Firestore document id.
 * @property userId Firebase Auth uid of the owner (or a local id until sign-in is ready).
 * @property venueId Geoapify place_id, or null when the venue was typed by hand.
 * @property venueName Name shown in the list and detail screens. Required.
 * @property address Optional street address of the venue.
 * @property latitude Venue latitude. 0.0 until a venue is picked from search (M3).
 * @property longitude Venue longitude. 0.0 until a venue is picked from search (M3).
 * @property category Restaurant, Event or Other.
 * @property rating 1 to 5 stars.
 * @property note Optional note, up to 200 characters.
 * @property createdAt When the check-in was first saved (epoch millis).
 * @property updatedAt When it was last changed (epoch millis). Used by the sync conflict rule.
 * @property synced false means this row still has to be pushed to Firestore.
 * @property deleted true means the user deleted it; the delete still has to be pushed.
 *
 * Kind: data class. Owner: Track B (Ana).
 */
@Entity(
    tableName = "checkins",
    // Every query filters by user, so an index on user_id keeps them fast
    indices = [Index(value = ["user_id"])]
)
data class Checkin(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "venue_id") val venueId: String? = null,
    @ColumnInfo(name = "venue_name") val venueName: String,
    @ColumnInfo(name = "address") val address: String? = null,
    @ColumnInfo(name = "latitude") val latitude: Double = 0.0,
    @ColumnInfo(name = "longitude") val longitude: Double = 0.0,
    @ColumnInfo(name = "category") val category: Category,
    @ColumnInfo(name = "rating") val rating: Int,
    @ColumnInfo(name = "note") val note: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @ColumnInfo(name = "synced", defaultValue = "0") val synced: Boolean = false,
    @ColumnInfo(name = "deleted", defaultValue = "0") val deleted: Boolean = false
)