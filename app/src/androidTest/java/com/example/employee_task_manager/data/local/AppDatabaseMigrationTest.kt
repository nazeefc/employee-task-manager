package com.example.employee_task_manager.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO tasks (id, clientId, title, completed, updatedAt, syncState) VALUES (1, 'client-1', 'Legacy Task', 0, 1000, 0)")
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true, AppDatabase.MIGRATION_1_2)

        val cursor = db.query("SELECT * FROM tasks WHERE id = 1")
        assertNotNull(cursor)
        val hasRow = cursor.moveToFirst()
        assertEquals(true, hasRow)

        val titleIndex = cursor.getColumnIndex("title")
        val descIndex = cursor.getColumnIndex("description")
        val priorityIndex = cursor.getColumnIndex("priority")
        val clientIdIndex = cursor.getColumnIndex("clientId")

        assertEquals("Legacy Task", cursor.getString(titleIndex))
        assertEquals("", cursor.getString(descIndex))
        assertEquals("Medium", cursor.getString(priorityIndex))
        assertEquals("client-1", cursor.getString(clientIdIndex))

        cursor.close()
    }
}
