package com.calsnap.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CalSnapApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_MEALS,
                    "Напоминания о еде",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Завтрак, обед, ужин" }
            )

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_WATER,
                    "Напоминания о воде",
                    NotificationManager.IMPORTANCE_LOW
                ).apply { description = "Пить воду" }
            )
        }
    }

    companion object {
        const val CHANNEL_MEALS = "calsnap_meals"
        const val CHANNEL_WATER = "calsnap_water"
    }
}
