package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private val notificationManager by lazy {
        ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val fileName = intent.getStringExtra("fileName")
        val isCompleted = intent.getBooleanExtra("isCompleted", false)
        val uri = intent.getStringExtra("uri")
        val downloadResult = if(isCompleted){
            getString(R.string.succeeded_to_download)
        } else {
            getString(R.string.failed_to_download)
        }
        
        notificationManager.cancel(0)

    }

}
