package org.odoj.onedayonejuzapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.odoj.onedayonejuzapp.R.drawable
import org.odoj.onedayonejuzapp.activity.ChatLogActivity

class Notifikasi(val context: Context) {
    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_NAME = "chat name"
    }

    fun createNotificatiion(){

        createNofiticationChannel()

        val intent = Intent(context, ChatLogActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(context,0,intent,0)

        val icon = BitmapFactory.decodeResource(context.resources, drawable.icon_home)

        val notification  = NotificationCompat.Builder(context, CHANNEL_NAME)
            .setSmallIcon(drawable.logo3)
            .setLargeIcon(icon)
            .setContentTitle("kontent titel")
            .setContentText("ini text nya")
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID,notification)
    }


    fun createNofiticationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MAKAN"
            val important = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_NAME, name, important).apply {
                description = "ya coba aja dulu"
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}