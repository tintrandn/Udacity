package com.udacity.project4.locationreminders.reminderslist

import java.io.Serializable
import java.util.*

/**
 * data class acts as a data mapper between the DB and the UI
 */
data class ReminderDataItem(
    var title: String?,
    var description: String? = "",
    var location: String?,
    var latitude: Double? = 15.995400906322162,
    var longitude: Double? = 07.99661937762035,
    val id: String = UUID.randomUUID().toString()
) : Serializable