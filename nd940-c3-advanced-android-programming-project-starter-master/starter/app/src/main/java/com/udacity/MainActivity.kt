package com.udacity

import android.app.DownloadManager
import android.app.DownloadManager.STATUS_FAILED
import android.app.DownloadManager.STATUS_SUCCESSFUL
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udacity.utils.cancelNotifications
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID = 0
    private lateinit var url: String

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(getString(R.string.notification_channel_name))

        radio_group.setOnCheckedChangeListener { _, checkedId ->
            // Responds to child RadioButton checked/unchecked
            when (checkedId) {
                R.id.radio_button_1 -> {
                    url = GLIDE_URL
                    downloadID = 1
                }
                R.id.radio_button_2 -> {
                    url = REPO_URL
                    downloadID = 2
                }
                R.id.radio_button_3 -> {
                    url = RETROFIT_URL
                    downloadID = 3
                }
            }
            Log.d("MainActivity", "Url $url")
        }

        loading_button.setOnClickListener {
            download()
        }

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query()
            var isSuccess = false
            query.setFilterById(id!!)
            query.setFilterByStatus(
                STATUS_FAILED or STATUS_SUCCESSFUL
            )
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    STATUS_SUCCESSFUL -> {
                        isSuccess = true
                    }
                    STATUS_FAILED -> {
                        isSuccess = false
                    }
                }
            }

            loading_button.buttonState = ButtonState.Completed
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.cancelNotifications()
            notificationManager.sendNotification(
                getMessageBody(),
                applicationContext,
                downloadID,
                isSuccess
            )
        }
    }

    private fun createChannel(channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_channel_description)

            notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getMessageBody(): String {
        return when (downloadID) {
            1 -> {
                getString(R.string.notification_description_1)
            }
            2 -> {
                getString(R.string.notification_description_2)
            }
            3 -> {
                getString(R.string.notification_description_3)
            }
            else -> {
                getString(R.string.notification_description_1)
            }
        }
    }

    private fun download() {
        // Check if url is not create shows toast
        if (!::url.isInitialized) {
            Toast.makeText(this, R.string.select_file, Toast.LENGTH_SHORT).show()
            return
        }
        // Start download
        loading_button.buttonState = ButtonState.Loading
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide"
        private const val REPO_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit"
        internal const val CHANNEL_ID = "channelId"
    }

}
