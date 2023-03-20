package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.webkit.URLUtil
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private val notificationManager: NotificationManager by lazy {
        getSystemService(this, NotificationManager::class.java) as NotificationManager
    }
    private val downloadManager: DownloadManager by lazy {
        getSystemService(DOWNLOAD_SERVICE) as DownloadManager
    }
    private var url = ""
    private var fileName: String = ""
    private var savedUrl = ""
    private var savedFileName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        notificationManager.createNotificationChannel(applicationContext)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
            when (radioGroup.checkedRadioButtonId) {
                -1 -> {
                    Toast.makeText(
                        this,
                        "You have to select at least one field",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                R.id.download_glide -> {
                    url = URL_GLIDE
                    fileName = "Glide"
                }
                R.id.download_this_app -> {
                    url = URL_LOAD_APP
                    fileName = "Load App"
                }
                R.id.download_retrofit -> {
                    url = URL_RETROFIT
                    fileName = "Retrofit"
                }
                else -> Toast.makeText(this, "Wrong url", Toast.LENGTH_SHORT).show()
            }
            if (URLUtil.isValidUrl(url)) download()
            radioGroup.clearCheck()
            savedUrl = url
            savedFileName = fileName
            url = ""
            fileName = ""
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(
                        cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    )
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            notificationManager.sendNotification(
                                savedFileName,
                                true,
                                savedUrl,
                                applicationContext
                            )
                            custom_button.buttonState = ButtonState.Completed

                        }
                        DownloadManager.STATUS_FAILED -> {
                            notificationManager.sendNotification(
                                savedFileName,
                                false,
                                savedUrl,
                                applicationContext
                            )
                            custom_button.buttonState = ButtonState.Completed
                        }
                        DownloadManager.STATUS_PENDING -> {
                            Toast.makeText(
                                this@MainActivity,
                                "The loading is pending",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        DownloadManager.STATUS_PAUSED -> {
                            Toast.makeText(
                                this@MainActivity,
                                "The loading is paused",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$fileName.zip")

        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }


    companion object {
        private const val URL_LOAD_APP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_GLIDE =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/master.zip"

    }

}
