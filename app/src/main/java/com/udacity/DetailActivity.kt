package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
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
        val resultColor: Int
        val downloadResult: String
        val fileName = intent.getStringExtra("fileName")
        val isCompleted = intent.getBooleanExtra("isCompleted", false)
        val uri = intent.getStringExtra("uri")
        val header = findViewById<ImageView>(R.id.header_detail)
        if(isCompleted){
            header.setImageResource(R.drawable.ic_baseline_file_download_done_24)
            downloadResult = getString(R.string.success)
            resultColor = Color.GREEN
        } else {
            header.setImageResource(R.drawable.ic_baseline_file_download_off_24)
            downloadResult = getString(R.string.fail)
            resultColor = Color.RED
        }
        findViewById<TextView>(R.id.file_name_value).text = fileName
        findViewById<TextView>(R.id.download_status_value).apply {
            text = downloadResult
            setTextColor(resultColor)
        }
        findViewById<TextView>(R.id.url_value).text = uri
        notificationManager.cancel(0)
        findViewById<Button>(R.id.ok_button).setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(backIntent)
        }
    }

}
