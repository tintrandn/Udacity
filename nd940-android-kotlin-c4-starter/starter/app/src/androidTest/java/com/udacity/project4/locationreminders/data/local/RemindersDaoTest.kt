package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    private val reminders =
        listOf(
            ReminderDTO(
                title = "title1",
                description = "description1",
                location = "location1",
                latitude = 1.1,
                longitude = 2.2
            ),
            ReminderDTO(
                title = "title2",
                description = "description2",
                location = "location2",
                latitude = 3.3,
                longitude = 4.4
            )
        )

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun getReminders() = runBlockingTest {
        database.reminderDao().saveReminder(reminders[0])
        database.reminderDao().saveReminder(reminders[1])
        val result = database.reminderDao().getReminders()
        assertThat(result[0].id, `is`(reminders[0].id))
        assertThat(result[0].title, `is`(reminders[0].title))
        assertThat(result[0].description, `is`(reminders[0].description))
        assertThat(result[0].latitude, `is`(reminders[0].latitude))
        assertThat(result[0].longitude, `is`(reminders[0].longitude))
        assertThat(result[0].location, `is`(reminders[0].location))
        assertThat(result[1].id, `is`(reminders[1].id))
        assertThat(result[1].title, `is`(reminders[1].title))
        assertThat(result[1].description, `is`(reminders[1].description))
        assertThat(result[1].latitude, `is`(reminders[1].latitude))
        assertThat(result[1].longitude, `is`(reminders[1].longitude))
        assertThat(result[1].location, `is`(reminders[1].location))
    }

    @Test
    fun getReminderById() = runBlockingTest {
        database.reminderDao().saveReminder(reminders[0])
        database.reminderDao().saveReminder(reminders[1])
        val result = database.reminderDao().getReminderById(reminders[1].id)
        assertThat(result!!.id, `is`(reminders[1].id))
        assertThat(result.title, `is`(reminders[1].title))
        assertThat(result.title, `is`("title2"))
        assertThat(result.description, `is`(reminders[1].description))
        assertThat(result.latitude, `is`(reminders[1].latitude))
        assertThat(result.longitude, `is`(reminders[1].longitude))
        assertThat(result.location, `is`(reminders[1].location))
    }

    @Test
    fun saveReminder() = runBlockingTest {
        database.reminderDao().saveReminder(reminders[0])
        database.reminderDao().saveReminder(reminders[1])
        val result = database.reminderDao().getReminders()
        assertThat(result, notNullValue())
        assertThat(result, `is`(reminders))
        assertThat(result.size, `is`(2))
    }

    @Test
    fun deleteAllReminders() = runBlockingTest {
        database.reminderDao().saveReminder(reminders[0])
        database.reminderDao().saveReminder(reminders[1])
        database.reminderDao().deleteAllReminders()
        val result = database.reminderDao().getReminders()
        assertThat(result, `is`(emptyList()))
    }
}