package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeDataSource(var remindersList: MutableList<ReminderDTO> = mutableListOf()) : ReminderDataSource {

    var shouldReturnError = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error(
                "Getting reminders failed. Try again!"
            )
        }
        return Result.Success(remindersList)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return if (shouldReturnError) {
            Result.Error("Error getting reminder with id $id")
        } else {
            val reminder = remindersList.find { it.id == id }
            if (reminder != null) {
                Result.Success(reminder)
            } else {
                Result.Error("Reminder not found", 404)
            }
        }
    }

    override suspend fun deleteAllReminders() {
        remindersList.clear()
    }


}