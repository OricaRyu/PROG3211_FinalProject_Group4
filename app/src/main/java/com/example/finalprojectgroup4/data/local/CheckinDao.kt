package com.example.finalprojectgroup4.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finalprojectgroup4.data.model.Category
import com.example.finalprojectgroup4.data.model.Checkin
import kotlinx.coroutines.flow.Flow

/**
 * The SQL for the checkins table. Room writes the code behind each function at build time
 * and checks every query against the table, so a typo in a column name fails the build.
 *
 * Kind: interface (Room @Dao). Owner: Track B (Ana).
 */
@Dao
interface CheckinDao {

    // ---------- Create ----------

    /** Inserts a new check-in. ABORT = fail if the id already exists (UUIDs never clash). */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(checkin: Checkin)

    // ---------- Read ----------

    /** All of this user's check-ins, newest first. Updates by itself on every change. */
    @Query("SELECT * FROM checkins WHERE user_id = :userId AND deleted = 0 ORDER BY created_at DESC")
    fun observeAll(userId: String): Flow<List<Checkin>>

    /** One check-in; emits null if it is missing, belongs to someone else, or was deleted. */
    @Query("SELECT * FROM checkins WHERE id = :id AND user_id = :userId AND deleted = 0")
    fun observeById(id: String, userId: String): Flow<Checkin?>

    /** One check-in, read once (used to pre-fill the Edit form). */
    @Query("SELECT * FROM checkins WHERE id = :id AND user_id = :userId AND deleted = 0")
    suspend fun getById(id: String, userId: String): Checkin?

    /** This user's check-ins in one category, newest first. */
    @Query(
        "SELECT * FROM checkins WHERE user_id = :userId AND deleted = 0 " +
                "AND category = :category ORDER BY created_at DESC"
    )
    fun observeByCategory(userId: String, category: Category): Flow<List<Checkin>>

    /** Rows that still have to be sent to Firestore, deleted ones included. For Track C. */
    @Query("SELECT * FROM checkins WHERE user_id = :userId AND synced = 0")
    suspend fun getUnsynced(userId: String): List<Checkin>

    // ---------- Update ----------

    /**
     * Saves an edit. Only the fields the user can edit change; id, owner and createdAt never do.
     * Always flags the row for sync, so no caller can forget.
     * Returns rows changed: 0 if the row is missing, deleted, or not this user's.
     */
    @Query(
        "UPDATE checkins SET venue_id = :venueId, venue_name = :venueName, address = :address, " +
                "latitude = :latitude, longitude = :longitude, category = :category, rating = :rating, " +
                "note = :note, updated_at = :now, synced = 0 " +
                "WHERE id = :id AND user_id = :userId AND deleted = 0"
    )
    suspend fun update(
        id: String,
        userId: String,
        venueId: String?,
        venueName: String,
        address: String?,
        latitude: Double,
        longitude: Double,
        category: Category,
        rating: Int,
        note: String?,
        now: Long
    ): Int

    /**
     * Marks a row as pushed to Firestore, but only if it was not edited or deleted while the push
     * was running. [pushedUpdatedAt] is the updatedAt of the copy that was sent.
     * Returns 0 when the row changed meanwhile: it stays unsynced and goes out on the next sync.
     * For Track C.
     */
    @Query(
        "UPDATE checkins SET synced = 1 " +
                "WHERE id = :id AND user_id = :userId AND updated_at = :pushedUpdatedAt"
    )
    suspend fun markSynced(id: String, userId: String, pushedUpdatedAt: Long): Int

    // ---------- Delete ----------

    /**
     * Soft delete: hides the row everywhere and flags it for sync.
     * Returns rows changed: deleting the same row twice returns 0, so it is not pushed again.
     */
    @Query(
        "UPDATE checkins SET deleted = 1, synced = 0, updated_at = :now " +
                "WHERE id = :id AND user_id = :userId AND deleted = 0"
    )
    suspend fun markDeleted(id: String, userId: String, now: Long): Int

    /**
     * Removes a row for good, but only one that was already soft-deleted, so a live check-in
     * can never be lost. Track C calls this after the delete reaches Firestore (#41).
     */
    @Query("DELETE FROM checkins WHERE id = :id AND user_id = :userId AND deleted = 1")
    suspend fun hardDelete(id: String, userId: String): Int
}