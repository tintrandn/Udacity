package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest
    : AutoCloseKoinTest() {

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var appContext: Application
    private lateinit var remindersDao: RemindersDao

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
    fun initRepository() {
        stopKoin()
        appContext = getApplicationContext()

        val testModules = module {

            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }

            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }

            single { RemindersLocalRepository(get()) as ReminderDataSource }

            // in-memory database dao
            single {
                Room.inMemoryDatabaseBuilder(
                    appContext,
                    RemindersDatabase::class.java
                )
                    .allowMainThreadQueries()
                    .build()
                    .reminderDao()
            }
        }

        startKoin {
            androidContext(appContext)
            modules(listOf(testModules))
        }

        remindersDao = get()
        runBlockingTest {
            remindersDao.saveReminder(reminders[0])
            remindersDao.saveReminder(reminders[1])
        }
    }

    @After
    fun clear () = runBlockingTest{
        remindersDao.deleteAllReminders()
    }

    @Test
    fun onClickReminderItem_navToReminderDescriptionActivity() {
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withId(R.id.reminderssRecyclerView))
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )
        onView(withText(R.string.reminder_detail)).check(matches(isDisplayed()))
        onView(withText(appContext.getString(R.string.detail_title, reminders[0].title)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun onClickAddReminderIcon_navToSaveReminderFragment() {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    @Test
    fun onResume_loadReminders_DisplayedInUI() = runBlockingTest {
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withId(R.id.reminderssRecyclerView))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))

        onView(withText(reminders[0].title)).check(matches(isDisplayed()))
        onView(withText(reminders[0].description)).check(matches(isDisplayed()))
        onView(withText(reminders[0].location)).check(matches(isDisplayed()))

        onView(withId(R.id.reminderssRecyclerView))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(1))
        onView(withText(reminders[1].title)).check(matches(isDisplayed()))
        onView(withText(reminders[1].description)).check(matches(isDisplayed()))
        onView(withText(reminders[1].location)).check(matches(isDisplayed()))
    }

    @Test
    fun pullToRefresh_showNoDataReminders() = runBlockingTest {
        launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        onView(withId(R.id.reminderssRecyclerView))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
        onView(withText(reminders[0].title)).check(matches(isDisplayed()))

        remindersDao.deleteAllReminders()

        onView(withId(R.id.refreshLayout)).perform(swipeDown())
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }
}