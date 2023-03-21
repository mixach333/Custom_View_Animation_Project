package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.URLUtil
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val enterFileNameEditText = findViewById<EditText>(R.id.enter_file_name_edit_text)
        val enterUrlScrollView = findViewById<ScrollView>(R.id.enter_url_scroll_view)
        val enterUrlEditText = findViewById<EditText>(R.id.enter_url_edit_text)
        val customUrlRadioButton = findViewById<RadioButton>(R.id.download_custom_radio_button)
        customUrlRadioButton.setOnClickListener {
            if (customUrlRadioButton.isChecked) {
                enterFileNameEditText.visibility = View.VISIBLE
                enterUrlScrollView.visibility = View.VISIBLE
            }
        }
        custom_button.setOnClickListener {
            when (radioGroup.checkedRadioButtonId) {

                R.id.download_glide -> {
                    url = URL_GLIDE
                    fileName = getString(R.string.download_glide_text)
                }
                R.id.download_this_app -> {
                    url = URL_LOAD_APP
                    fileName = getString(R.string.download_this_app_text)
                }
                R.id.download_retrofit -> {
                    url = URL_RETROFIT
                    fileName = getString(R.string.download_retrofit_text)
                }
                R.id.download_custom_radio_button -> checkCustomFields(
                    enterFileNameEditText.text.toString(),
                    enterUrlEditText.text.toString()
                )

                else -> {
                    Toast.makeText(
                        this,
                        "You have to select at least one field",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            if (URLUtil.isValidUrl(url)) {
                download()
                radioGroup.clearCheck()
                enterFileNameEditText.visibility = View.INVISIBLE
                enterFileNameEditText.text.clear()
                enterUrlScrollView.visibility = View.INVISIBLE
                enterUrlEditText.text.clear()
                savedUrl = url
                savedFileName = fileName
                url = ""
                fileName = ""
            }
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
        val fileExtensionRegex = Regex("\\.[a-zA-Z0-9]+$")
        val subPath = if(fileName.contains(fileExtensionRegex)) fileName else "$fileName.zip"
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, subPath)

        downloadID =
            downloadManager.enqueue(request)
    }

    private fun checkCustomFields(fileNameToCheck: String, urlToCheck: String) {
        if (!(fileNameToCheck.length > 4 )) {
            showShortToast("The size of file name should be at least 5 characters including the file format which ends with dot: \".\", for example \"2.jpg\"")
            return
        } else if (!URLUtil.isValidUrl(urlToCheck)) {
            showShortToast("Your URL is wrong, try again or select another download option")
            return
        }
        fileName = fileNameToCheck
        url = urlToCheck
    }

    private fun showShortToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
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
