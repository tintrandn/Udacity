package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.udacity.project4.locationreminders.rule.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeDataSource: FakeDataSource

    private lateinit var viewModel: RemindersListViewModel

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
    fun setupViewModel() {
        fakeDataSource = FakeDataSource()
        viewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }

    @After
    fun clear() {
        stopKoin()
    }

    @Test
    fun loadReminders_showLoading_returnEmptyData_showNoData() {
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()

        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(viewModel.showNoData.getOrAwaitValue(), `is`(true))
        Truth.assertThat(viewModel.remindersList.value).isEqualTo(emptyList<ReminderDataItem>())
    }

    @Test
    fun loadReminders_showLoading_returnReminderList_showData() = runBlockingTest {
        fakeDataSource.saveReminder(reminders[0])
        fakeDataSource.saveReminder(reminders[1])
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()

        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(viewModel.showNoData.getOrAwaitValue(), `is`(false))
        Truth.assertThat(viewModel.remindersList.value)
            .isEqualTo(reminders.toListReminderDataItem())
    }

    @Test
    fun loadReminders_showLoading_returnError_showSnackBar() {
        fakeDataSource.shouldReturnError = true
        mainCoroutineRule.pauseDispatcher()

        viewModel.loadReminders()
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
        Truth.assertThat(viewModel.showSnackBar.getOrAwaitValue())
            .isEqualTo("Getting reminders failed. Try again!")
    }

    private fun List<ReminderDTO>.toListReminderDataItem(): ArrayList<ReminderDataItem> {
        val dataList = ArrayList<ReminderDataItem>()
        dataList.addAll((this).map { reminder ->
            ReminderDataItem(
                reminder.title,
                reminder.description,
                reminder.location,
                reminder.latitude,
                reminder.longitude,
                reminder.id
            )
        })
        return dataList
    }

}