package com.example.traveldiary.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.UUID

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE travel_entries ADD COLUMN uuid TEXT NOT NULL DEFAULT ''")
        db.query("SELECT id FROM travel_entries").use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getInt(0)
                db.execSQL(
                    "UPDATE travel_entries SET uuid = ? WHERE id = ?",
                    arrayOf(UUID.randomUUID().toString(), id)
                )
            }
        }
    }
}
