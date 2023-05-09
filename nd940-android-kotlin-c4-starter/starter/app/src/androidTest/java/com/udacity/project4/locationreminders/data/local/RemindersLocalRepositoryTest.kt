package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersLocalRepository: RemindersLocalRepository

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
    fun setup() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        remindersLocalRepository = RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Main
        )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminder_getReminders_returnSuccess() = runBlocking {
        remindersLocalRepository.saveReminder(reminders[0])
        remindersLocalRepository.saveReminder(reminders[1])

        val result = remindersLocalRepository.getReminders() as? Result.Success

        assertThat(result is Result.Success, `is`(true))
        result as Result.Success
        assertThat(result.data[0].title, `is`(reminders[0].title))
        assertThat(result.data[0].title, `is`("title1"))
        assertThat(result.data[0].description, `is`(reminders[0].description))
        assertThat(result.data[0].latitude, `is`(reminders[0].latitude))
        assertThat(result.data[0].longitude, `is`(reminders[0].longitude))
        assertThat(result.data[0].location, `is`(reminders[0].location))
        assertThat(result.data[1].title, `is`(reminders[1].title))
        assertThat(result.data[1].title, `is`("title2"))
        assertThat(result.data[1].description, `is`(reminders[1].description))
        assertThat(result.data[1].latitude, `is`(reminders[1].latitude))
        assertThat(result.data[1].longitude, `is`(reminders[1].longitude))
        assertThat(result.data[1].location, `is`(reminders[1].location))
    }

    @Test
    fun saveReminder_getReminderById_returnSuccess() = runBlocking {
        remindersLocalRepository.saveReminder(reminders[0])
        remindersLocalRepository.saveReminder(reminders[1])

        val result = remindersLocalRepository.getReminder(reminders[1].id) as? Result.Success

        assertThat(result is Result.Success, `is`(true))
        result as Result.Success
        assertThat(result.data.title, `is`(reminders[1].title))
        assertThat(result.data.title, `is`("title2"))
        assertThat(result.data.description, `is`(reminders[1].description))
        assertThat(result.data.latitude, `is`(reminders[1].latitude))
        assertThat(result.data.longitude, `is`(reminders[1].longitude))
        assertThat(result.data.location, `is`(reminders[1].location))
    }

    @Test
    fun saveReminder_getReminderById_returnError() = runBlocking {
        remindersLocalRepository.saveReminder(reminders[0])
        remindersLocalRepository.saveReminder(reminders[1])

        remindersLocalRepository.deleteAllReminders()

        val result = remindersLocalRepository.getReminder(reminders[1].id)

        assertThat(result is Result.Error, `is`(true))
        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }

    @Test
    fun deleteAllReminders_returnEmptyList() = runBlocking {
        remindersLocalRepository.saveReminder(reminders[0])
        remindersLocalRepository.saveReminder(reminders[1])

        remindersLocalRepository.deleteAllReminders()

        val result = remindersLocalRepository.getReminders()

        assertThat(result is Result.Success, `is`(true))
        result as Result.Success

        assertThat(result.data, `is`(emptyList()))
    }
}