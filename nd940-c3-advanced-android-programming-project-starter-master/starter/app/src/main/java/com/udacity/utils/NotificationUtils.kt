package com.udacity.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.DetailActivity.Companion.DETAIL_ACTIVITY_INTENT_DOWNLOAD_ID
import com.udacity.DetailActivity.Companion.DETAIL_ACTIVITY_INTENT_IS_SUCCESS_KEY
import com.udacity.MainActivity
import com.udacity.R

// Notification ID.
private const val NOTIFICATION_ID = 0

/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    downloadId: Int,
    isSuccess: Boolean
) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(DETAIL_ACTIVITY_INTENT_DOWNLOAD_ID, downloadId)
    contentIntent.putExtra(DETAIL_ACTIVITY_INTENT_IS_SUCCESS_KEY, isSuccess)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        MainActivity.CHANNEL_ID
    ).setSmallIcon(R.drawable.ic_notification_small_24dp)
        .setContentTitle(
            applicationContext
                .getString(R.string.notification_title)
        )
        .setContentText(messageBody)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_notification_small_24dp,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}