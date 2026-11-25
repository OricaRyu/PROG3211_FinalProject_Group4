package com.example.finalprojectgroup4.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    /** Replaces every field of an existing check-in. Returns how many rows changed (0 or 1). */
    @Update
    suspend fun update(checkin: Checkin): Int

    /** Marks a row as pushed to Firestore. For Track C. */
    @Query("UPDATE checkins SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    // ---------- Delete ----------

    /** Soft delete: hides the row everywhere and flags it for sync. Returns rows changed. */
    @Query(
        "UPDATE checkins SET deleted = 1, synced = 0, updated_at = :now " +
                "WHERE id = :id AND user_id = :userId"
    )
    suspend fun markDeleted(id: String, userId: String, now: Long): Int

    /** Removes the row for good. Track C calls this after the delete reaches Firestore (#41). */
    @Query("DELETE FROM checkins WHERE id = :id")
    suspend fun hardDelete(id: String): Int
}