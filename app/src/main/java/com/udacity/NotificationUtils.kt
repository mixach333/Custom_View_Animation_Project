package com.udacity

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendNotification(
    fileName: String,
    isCompleted: Boolean,
    uri: String,
    appContext: Context
) {
    val notificationManager = ContextCompat.getSystemService(
        appContext,
        NotificationManager::class.java
    ) as NotificationManager

    val mainContentIntent = Intent(appContext, MainActivity::class.java)
    val mainPendingIntent = PendingIntent.getActivity(
        appContext,
        0,
        mainContentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val detailContentIntent = Intent(appContext, DetailActivity::class.java)
    detailContentIntent.putExtra("fileName", fileName)
    detailContentIntent.putExtra("isCompleted", isCompleted)
    detailContentIntent.putExtra("uri", uri)
    val detailPendingIntent = PendingIntent.getActivity(
        appContext,
        0,
        detailContentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val messageBody = if (isCompleted) {
        appContext.getString(R.string.notification_download_success)
    } else{
        appContext.getString(R.string.notification_download_failed)
    }

    val builder = NotificationCompat.Builder(appContext, appContext.getString(R.string.notification_channel_id))
        .setAutoCancel(true)
        .setContentIntent(mainPendingIntent)
        .setContentTitle(appContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSmallIcon(when(isCompleted){
            true -> R.drawable.ic_baseline_file_download_done_24
            else -> R.drawable.ic_baseline_file_download_off_24
        })
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            appContext.getString(R.string.notification_button),
            detailPendingIntent
        )
    notify(0, builder.build())
}

fun NotificationManager.createNotificationChannel(appContext: Context) {
    val notificationManager = ContextCompat.getSystemService(
        appContext,
        NotificationManager::class.java
    ) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            appContext.getString(R.string.notification_channel_id),
            appContext.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(true)
        notificationChannel.description = appContext.getString(R.string.notification_download_status)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}