package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.rule.MainCoroutineRule

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeDataSource: FakeDataSource

    private lateinit var viewModel: SaveReminderViewModel

    private val reminderNormal =
        ReminderDataItem(
            title = "title1",
            description = "description1",
            location = "location1",
            latitude = 1.1,
            longitude = 2.2
        )

    private val reminderWithoutTitle =
            ReminderDataItem(
                title = null,
                description = "description1",
                location = "location1",
                latitude = 1.1,
                longitude = 2.2
            )

    private val reminderWithoutLocation =
        ReminderDataItem(
            title = "title1",
            description = "description1",
            location = null,
            latitude = 1.1,
            longitude = 2.2
        )

    @Before
    fun setupViewModel() {
        fakeDataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }

    @After
    fun clear() {
        stopKoin()
    }

    @Test
    fun validateAndSaveReminder_showLoading_returnReminderList_showToast() {
        mainCoroutineRule.pauseDispatcher()
        viewModel.validateAndSaveReminder(reminderNormal)

        MatcherAssert.assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
        Truth.assertThat(viewModel.showToast.getOrAwaitValue())
            .isEqualTo("Reminder Saved !")
    }

    @Test
    fun validateAndSaveReminder_notShowLoading_returnReminderWithoutTitle_showSnackBar() {
        mainCoroutineRule.pauseDispatcher()

        viewModel.validateAndSaveReminder(reminderWithoutTitle)

        mainCoroutineRule.resumeDispatcher()

        Truth.assertThat(viewModel.showSnackBarInt.value)
            .isEqualTo(R.string.err_enter_title)
    }

    @Test
    fun validateAndSaveReminder_notShowLoading_returnReminderWithoutLocation_showSnackBar() {
        mainCoroutineRule.pauseDispatcher()

        viewModel.validateAndSaveReminder(reminderWithoutLocation)

        mainCoroutineRule.resumeDispatcher()

        Truth.assertThat(viewModel.showSnackBarInt.value)
            .isEqualTo(R.string.err_select_location)
    }
}