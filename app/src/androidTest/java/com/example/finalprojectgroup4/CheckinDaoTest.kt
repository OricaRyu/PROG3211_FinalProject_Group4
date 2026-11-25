package com.example.finalprojectgroup4

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.finalprojectgroup4.data.local.AppDatabase
import com.example.finalprojectgroup4.data.local.CheckinDao
import com.example.finalprojectgroup4.data.model.Category
import com.example.finalprojectgroup4.data.model.Checkin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Runs the Room CRUD on a real device or emulator, on a separate test database file.
 * Run it from Android Studio: right-click this file > Run.
 *
 * Kind: instrumented test class. Owner: Track B (Ana).
 */
@RunWith(AndroidJUnit4::class)
class CheckinDaoTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var db: AppDatabase
    private lateinit var dao: CheckinDao

    private fun openDb() {
        db = Room.databaseBuilder(context, AppDatabase::class.java, TEST_DB).build()
        dao = db.checkinDao()
    }

    private fun sample(id: String, user: String = "user-a", category: Category = Category.RESTAURANT) =
        Checkin(
            id = id,
            userId = user,
            venueName = "Venue $id",
            category = category,
            rating = 4,
            note = "Test note",
            createdAt = 1_000L,
            updatedAt = 1_000L
        )

    /** Shortcut for an edit of check-in [id] that only changes rating and note. */
    private suspend fun edit(id: String, user: String = "user-a", rating: Int, note: String, now: Long) =
        dao.update(
            id = id, userId = user,
            venueId = null, venueName = "Venue $id", address = null,
            latitude = 0.0, longitude = 0.0,
            category = Category.RESTAURANT, rating = rating, note = note,
            now = now
        )

    @Before
    fun setUp() {
        context.deleteDatabase(TEST_DB)
        openDb()
    }

    @After
    fun tearDown() {
        db.close()
        context.deleteDatabase(TEST_DB)
    }

    // ---------- CRUD ----------

    @Test
    fun insertThenReadById() = runBlocking {
        dao.insert(sample("1"))
        assertEquals("Venue 1", dao.getById("1", "user-a")?.venueName)
    }

    @Test
    fun readByCategory() = runBlocking {
        dao.insert(sample("1", category = Category.RESTAURANT))
        dao.insert(sample("2", category = Category.EVENT))
        val events = dao.observeByCategory("user-a", Category.EVENT).first()
        assertEquals(listOf("2"), events.map { it.id })
    }

    @Test
    fun updateChangesFields() = runBlocking {
        dao.insert(sample("1"))
        assertEquals(1, edit("1", rating = 2, note = "Changed", now = 2_000L))
        val saved = dao.getById("1", "user-a")
        assertEquals(2, saved?.rating)
        assertEquals("Changed", saved?.note)
        assertEquals(2_000L, saved?.updatedAt)
        assertEquals(1_000L, saved?.createdAt) // createdAt never changes
    }

    @Test
    fun deleteHidesRowButKeepsItForSync() = runBlocking {
        dao.insert(sample("1"))
        assertEquals(1, dao.markDeleted("1", "user-a", now = 2_000L))
        assertNull(dao.getById("1", "user-a"))
        assertTrue(dao.getUnsynced("user-a").any { it.id == "1" && it.deleted })
    }

    @Test
    fun usersOnlySeeTheirOwnRows() = runBlocking {
        dao.insert(sample("1", user = "user-a"))
        dao.insert(sample("2", user = "user-b"))
        assertEquals(listOf("1"), dao.observeAll("user-a").first().map { it.id })
        assertNull(dao.getById("2", "user-a"))
    }

    @Test
    fun usersCannotEditOthersRows() = runBlocking {
        dao.insert(sample("2", user = "user-b"))
        assertEquals(0, edit("2", user = "user-a", rating = 1, note = "Hacked", now = 2_000L))
        assertEquals(4, dao.getById("2", "user-b")?.rating)
    }

    @Test
    fun dataSurvivesReopening() = runBlocking {
        dao.insert(sample("1"))
        db.close()
        openDb() // like restarting the app
        assertEquals(1, dao.observeAll("user-a").first().size)
    }

    // ---------- Sync rules (from review) ----------

    @Test
    fun editOfSyncedRowIsFlaggedForSync() = runBlocking {
        dao.insert(sample("1").copy(synced = true))
        edit("1", rating = 2, note = "Changed", now = 2_000L)
        assertTrue(dao.getUnsynced("user-a").any { it.id == "1" })
    }

    @Test
    fun markSyncedWorksWhenRowUnchanged() = runBlocking {
        dao.insert(sample("1")) // updated_at = 1000
        assertEquals(1, dao.markSynced("1", "user-a", pushedUpdatedAt = 1_000L))
        assertTrue(dao.getUnsynced("user-a").isEmpty())
    }

    @Test
    fun markSyncedIgnoresRowChangedDuringPush() = runBlocking {
        dao.insert(sample("1"))                       // pushed with updated_at = 1000
        dao.markDeleted("1", "user-a", now = 2_000L)  // changed during the push
        assertEquals(0, dao.markSynced("1", "user-a", pushedUpdatedAt = 1_000L))
        assertTrue(dao.getUnsynced("user-a").any { it.id == "1" })
    }

    @Test
    fun hardDeleteKeepsLiveRows() = runBlocking {
        dao.insert(sample("1"))
        assertEquals(0, dao.hardDelete("1", "user-a"))
        assertEquals("1", dao.getById("1", "user-a")?.id)
    }

    @Test
    fun hardDeleteRemovesSoftDeletedRow() = runBlocking {
        dao.insert(sample("1"))
        dao.markDeleted("1", "user-a", now = 2_000L)
        assertEquals(1, dao.hardDelete("1", "user-a"))
        assertTrue(dao.getUnsynced("user-a").isEmpty())
    }

    @Test
    fun deletingTwiceOnlyCountsOnce() = runBlocking {
        dao.insert(sample("1"))
        assertEquals(1, dao.markDeleted("1", "user-a", now = 2_000L))
        assertEquals(0, dao.markDeleted("1", "user-a", now = 3_000L))
    }

    private companion object {
        const val TEST_DB = "spot_test.db"
    }
}