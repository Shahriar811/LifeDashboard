package com.example.lifedashboard

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.lifedashboard.data.AppDatabase
import com.example.lifedashboard.data.GoalsRepository
import com.example.lifedashboard.data.UserPreferencesRepository

class MainApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val goalsRepository: GoalsRepository by lazy { GoalsRepository(this) }
    val userPreferencesRepository: UserPreferencesRepository by lazy { UserPreferencesRepository(this) }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            
            // Task reminders channel
            val taskChannel = NotificationChannel("task_reminders", "Task Reminders", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notifications for task due dates"
            }
            notificationManager.createNotificationChannel(taskChannel)
            
            // Daily summary channel
            val summaryChannel = NotificationChannel("daily_summary", "Daily Summary", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Daily goal reminders"
            }
            notificationManager.createNotificationChannel(summaryChannel)
        }
        
        // Schedule daily summary
        DailySummaryScheduler(this).scheduleDailySummary()
    }
}