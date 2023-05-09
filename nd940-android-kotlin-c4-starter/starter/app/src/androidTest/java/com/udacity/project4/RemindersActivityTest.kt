package com.udacity.project4

import android.app.Activity
import android.app.Application
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not


@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    // An idling resource that waits for Data Binding to have no pending bindings.
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    private val reminder = ReminderDTO(
        title = "title1",
        description = "description1",
        location = "location1",
        latitude = 1.1,
        longitude = 2.2
    )

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
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
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @Before
    fun registerIdlingResources() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResources() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun loadReminders_returnReminderData_showReminders_addNewReminder_showUpdateReminders() =
        runBlocking {
            repository.saveReminder(reminder)

            val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
            val activity = getActivity(activityScenario)

            dataBindingIdlingResource.monitorActivity(activityScenario)

            onView(withId(R.id.reminderssRecyclerView)).perform(
                RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                    0
                )
            )

            onView(withText(reminder.title)).check(matches(isDisplayed()))

            onView(withId(R.id.addReminderFAB)).perform(ViewActions.click())
            onView(withId(R.id.saveReminder)).perform(ViewActions.click())

            var snackBarMessage = appContext.getString(R.string.err_enter_title)
            onView(withText(snackBarMessage)).check(matches(isDisplayed()))

            onView(withId(R.id.reminderTitle)).perform(ViewActions.replaceText("Title2"))

            // wait snack bar gone
            withContext(Dispatchers.IO) {
                Thread.sleep(3000)
            }

            onView(withId(R.id.saveReminder)).perform(ViewActions.click())
            snackBarMessage = appContext.getString(R.string.err_select_location)
            onView(withText(snackBarMessage)).check(matches(isDisplayed()))

            // wait snack bar gone
            withContext(Dispatchers.IO) {
                Thread.sleep(3000)
            }

            onView(withId(R.id.selectLocation)).perform(ViewActions.click())
            onView(withId(R.id.mapFragment)).perform(ViewActions.longClick())
            onView(withId(R.id.saveBtn)).perform(ViewActions.click())
            onView(withId(R.id.saveReminder)).perform(ViewActions.click())


            onView(withText(R.string.reminder_saved)).inRoot(
                withDecorView(not(`is`(activity.window.decorView)))
            ).check(matches(isDisplayed()))


            onView(withText("title1")).check(matches(isDisplayed()))
            onView(withText("Title2")).check(matches(isDisplayed()))

            onView(withId(R.id.reminderssRecyclerView))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0,
                        ViewActions.click()
                    )
                )
            onView(withText(R.string.reminder_detail)).check(matches(isDisplayed()))
            onView(withText(appContext.getString(R.string.detail_title, reminder.title)))
                .check(matches(isDisplayed()))
            onView(
                withText(
                    appContext.getString(
                        R.string.detail_description,
                        reminder.description
                    )
                )
            )
                .check(matches(isDisplayed()))
            onView(withText(appContext.getString(R.string.detail_location, reminder.location)))
                .check(matches(isDisplayed()))
            onView(
                withText(
                    appContext.getString(
                        R.string.lat_long_snippet, reminder.latitude,
                        reminder.longitude
                    )
                )
            )
                .check(matches(isDisplayed()))

            pressBack()
            activityScenario.close()
        }

    private fun getActivity(activityScenario: ActivityScenario<RemindersActivity>): Activity {
        lateinit var activity: Activity
        activityScenario.onActivity {
            activity = it
        }
        return activity
    }
}


