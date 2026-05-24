package com.example.lifedashboard

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

class TaskNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskText = intent.getStringExtra("task_text") ?: "You have a task due!"
        val taskId = intent.getIntExtra("task_id", 0)
        
        Log.d("TaskNotificationReceiver", "Alarm triggered for task: $taskText, ID: $taskId")

        try {
            val notification = NotificationCompat.Builder(context, "task_reminders")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Task Reminder")
                .setContentText(taskText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Use absolute value of taskId or generate unique ID if 0
            val notificationId = if (taskId != 0) Math.abs(taskId) else taskText.hashCode().and(0x7FFFFFFF)
            manager.notify(notificationId, notification)
            
            Log.d("TaskNotificationReceiver", "Notification shown with ID: $notificationId")
        } catch (e: Exception) {
            Log.e("TaskNotificationReceiver", "Error showing notification: ${e.message}", e)
        }
    }
}