package com.example.traveldiary.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val testDb = "migration-test"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        TravelDatabase::class.java,
    )

    @Test
    fun migrate2To3_preservesRowsAndPopulatesDistinctUuids() {
        helper.createDatabase(testDb, 2).apply {
            execSQL(
                "INSERT INTO travel_entries " +
                    "(title, location, country, date, tag, imageUrl, description, latitude, longitude, isFavourite) " +
                    "VALUES ('Kyoto', 'Kioto', 'Japon', 1759900000000, 'Culture', 'http://a/b.jpg', '', 35.0, 135.0, 1)"
            )
            execSQL(
                "INSERT INTO travel_entries " +
                    "(title, location, country, date, tag, imageUrl, description, latitude, longitude, isFavourite) " +
                    "VALUES ('Paris', 'Paris', 'Francia', 1759900000001, 'Trip', 'http://a/c.jpg', '', NULL, NULL, 0)"
            )
            close()
        }

        val db = helper.runMigrationsAndValidate(testDb, 3, true, MIGRATION_2_3)

        val uuids = mutableListOf<String>()
        val titles = mutableListOf<String>()
        db.query("SELECT title, uuid FROM travel_entries ORDER BY id").use { cursor ->
            while (cursor.moveToNext()) {
                titles += cursor.getString(0)
                uuids += cursor.getString(1)
            }
        }
        db.close()

        assertEquals(listOf("Kyoto", "Paris"), titles)
        assertEquals(2, uuids.size)
        assertTrue(uuids.all { it.isNotBlank() })
        assertNotEquals(uuids[0], uuids[1])
    }
}
