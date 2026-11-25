package com.example.finalprojectgroup4.data.repository

import android.content.Context
import com.example.finalprojectgroup4.data.local.AppDatabase
import com.example.finalprojectgroup4.data.local.CheckinDao
import com.example.finalprojectgroup4.data.model.Category
import com.example.finalprojectgroup4.data.model.Checkin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import java.util.UUID

/**
 * Single source of check-ins for the ViewModels: screens and ViewModels never touch Room directly.
 *
 * Kind: class. Owner: Track B (Ana), with Track C.
 */
@OptIn(ExperimentalCoroutinesApi::class) // for flatMapLatest
class CheckinRepository private constructor(
    private val dao: CheckinDao
) {

    companion object {
        /** Used until Firebase sign-in is wired in (#26, #28). */
        const val LOCAL_USER_ID = "local-user"

        @Volatile
        private var instance: CheckinRepository? = null

        /** Returns the app-wide repository, creating it the first time. */
        fun getInstance(context: Context): CheckinRepository =
            instance ?: synchronized(this) {
                instance ?: CheckinRepository(AppDatabase.getInstance(context).checkinDao())
                    .also { instance = it }
            }
    }

    private val _userId = MutableStateFlow(LOCAL_USER_ID)

    /** Who is signed in. Every read and write uses it, so users only see their own check-ins. */
    val userId: StateFlow<String> = _userId.asStateFlow()

    /** Track C: call after login with the Firebase uid, and after logout. Open screens switch automatically. */
    fun setUser(uid: String) {
        _userId.value = uid
    }

    // ---------- Read ----------
    // flatMapLatest: when the user changes, stop watching the old user's rows and watch the new one's.

    /** The signed-in user's check-ins, newest first. Deleted ones are already left out. */
    val checkins: Flow<List<Checkin>> =
        _userId.flatMapLatest { uid -> dao.observeAll(uid) }

    /** One category only, newest first. */
    fun observeByCategory(category: Category): Flow<List<Checkin>> =
        _userId.flatMapLatest { uid -> dao.observeByCategory(uid, category) }

    /** One check-in for the Detail screen; emits null once it is deleted or if it doesn't exist. */
    fun observe(id: String): Flow<Checkin?> =
        _userId.flatMapLatest { uid -> dao.observeById(id, uid) }

    /** One check-in, read once (for the Edit form). Null if missing or deleted. */
    suspend fun getById(id: String): Checkin? = dao.getById(id, _userId.value)

    // ---------- Write ----------

    /** Saves a new check-in and returns it. */
    suspend fun create(
        venueName: String,
        category: Category,
        rating: Int,
        note: String?,
        venueId: String? = null,
        address: String? = null,
        latitude: Double = 0.0,
        longitude: Double = 0.0
    ): Checkin {
        val now = System.currentTimeMillis()
        val checkin = Checkin(
            id = UUID.randomUUID().toString(),
            userId = _userId.value,
            venueId = venueId,
            venueName = venueName,
            address = address,
            latitude = latitude,
            longitude = longitude,
            category = category,
            rating = rating,
            note = note,
            createdAt = now,
            updatedAt = now,
            synced = false
        )
        dao.insert(checkin)
        return checkin
    }

    /**
     * Saves the user's edits to [checkin]. The DAO only changes the editable fields,
     * bumps updatedAt and flags it for sync. Returns false if it was missing, deleted or not theirs.
     */
    suspend fun update(checkin: Checkin): Boolean =
        dao.update(
            id = checkin.id,
            userId = _userId.value,
            venueId = checkin.venueId,
            venueName = checkin.venueName,
            address = checkin.address,
            latitude = checkin.latitude,
            longitude = checkin.longitude,
            category = checkin.category,
            rating = checkin.rating,
            note = checkin.note,
            now = System.currentTimeMillis()
        ) > 0

    /** Deletes a check-in (soft delete; the sync removes it for good afterwards). */
    suspend fun delete(id: String): Boolean =
        dao.markDeleted(id, _userId.value, System.currentTimeMillis()) > 0

    // ---------- For Track C's Firestore sync ----------

    /** Rows not yet pushed to Firestore, including deleted ones. */
    suspend fun getUnsynced(): List<Checkin> = dao.getUnsynced(_userId.value)

    /**
     * Call after [pushed] reached Firestore. Pass the same copy that was sent: if the row was
     * edited or deleted during the push, this returns false and it stays unsynced for next time.
     */
    suspend fun markSynced(pushed: Checkin): Boolean =
        dao.markSynced(pushed.id, _userId.value, pushedUpdatedAt = pushed.updatedAt) > 0

    /** Removes a soft-deleted row for good, after its delete reached Firestore. */
    suspend fun hardDelete(id: String): Boolean = dao.hardDelete(id, _userId.value) > 0
}