package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionLayout.TransitionListener
import com.udacity.utils.cancelNotifications
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val notificationManager = this.getSystemService(NotificationManager::class.java)
        notificationManager.cancelNotifications()

        val downloadID = intent.getIntExtra(DETAIL_ACTIVITY_INTENT_DOWNLOAD_ID, 1)
        name_subtitle.text = when (downloadID) {
            1 -> getString(R.string.label_1)
            2 -> getString(R.string.label_2)
            3 -> getString(R.string.label_3)
            else -> {
                getString(R.string.label_1)
            }
        }

        val isSuccess = intent.getBooleanExtra(DETAIL_ACTIVITY_INTENT_IS_SUCCESS_KEY, false)
        status_subtitle.apply {
            if (isSuccess) {
                text = getString(R.string.success)
                setTextColor(resources.getColor(android.R.color.holo_green_light, null))
            } else {
                text = getString(R.string.fail)
                setTextColor(resources.getColor(android.R.color.holo_red_light, null))
            }
        }

        ok_btn.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val DETAIL_ACTIVITY_INTENT_DOWNLOAD_ID = "download_id"
        const val DETAIL_ACTIVITY_INTENT_IS_SUCCESS_KEY = "is_success_key"
    }
}
