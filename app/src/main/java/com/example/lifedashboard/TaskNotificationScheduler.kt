package com.example.lifedashboard

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.lifedashboard.data.Task

class TaskNotificationScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    private val TAG = "TaskNotificationScheduler"

    fun schedule(task: Task) {
        if (task.dueDate == null || task.dueDate < System.currentTimeMillis()) {
            Log.d(TAG, "Cannot schedule: dueDate is null or in the past")
            return
        }

        // Ensure task has a valid ID (use hash as fallback for request code uniqueness)
        val requestCode = if (task.id > 0) task.id else {
            // Use hash of text + dueDate to ensure uniqueness if ID is 0
            (task.text.hashCode() + task.dueDate.hashCode()).and(0x7FFFFFFF)
        }

        // Check if exact alarms are allowed (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e(TAG, "Cannot schedule exact alarms - permission not granted")
                // Optionally: Open system settings to allow exact alarms
                return
            }
        }

        val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
            putExtra("task_text", task.text)
            putExtra("task_id", task.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val currentTime = System.currentTimeMillis()
            val timeUntilAlarm = task.dueDate - currentTime
            Log.d(TAG, "Scheduling alarm for task: ${task.text}, ID: ${task.id}, RequestCode: $requestCode")
            Log.d(TAG, "Alarm time: ${java.util.Date(task.dueDate)}, Time until: ${timeUntilAlarm}ms")

            // Use setExactAndAllowWhileIdle for better reliability on Android 6.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    task.dueDate,
                    pendingIntent
                )
                Log.d(TAG, "Alarm scheduled using setExactAndAllowWhileIdle")
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    task.dueDate,
                    pendingIntent
                )
                Log.d(TAG, "Alarm scheduled using setExact")
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    task.dueDate,
                    pendingIntent
                )
                Log.d(TAG, "Alarm scheduled using set")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when scheduling alarm: ${e.message}", e)
            e.printStackTrace()
        } catch (e: Exception) {
            Log.e(TAG, "Exception when scheduling alarm: ${e.message}", e)
            e.printStackTrace()
        }
    }

    fun cancel(task: Task) {
        // Use same request code calculation as schedule
        val requestCode = if (task.id > 0) task.id else {
            (task.text.hashCode() + (task.dueDate?.hashCode() ?: 0)).and(0x7FFFFFFF)
        }
        
        val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
            putExtra("task_text", task.text)
            putExtra("task_id", task.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Cancelled alarm for task ID: ${task.id}, RequestCode: $requestCode")
    }
}