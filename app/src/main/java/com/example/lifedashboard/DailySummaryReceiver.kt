package com.example.lifedashboard

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.lifedashboard.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class DailySummaryReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("DailySummaryReceiver", "Daily summary alarm triggered")
        
        scope.launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val goals = db.goalDao().getAllGoals().first()
                
                val today = Calendar.getInstance()
                val dailyGoals = goals.filter { goal ->
                    if (goal.type != "Daily") return@filter false
                    val goalDate = Calendar.getInstance().apply { timeInMillis = goal.creationDate }
                    today.get(Calendar.YEAR) == goalDate.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) == goalDate.get(Calendar.DAY_OF_YEAR)
                }
                
                val pendingGoals = dailyGoals.filter { goal ->
                    val goalDate = Calendar.getInstance().apply { timeInMillis = goal.creationDate }
                    val endOfDay = Calendar.getInstance().apply {
                        timeInMillis = goalDate.timeInMillis
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                    }
                    System.currentTimeMillis() < endOfDay.timeInMillis
                }
                
                if (pendingGoals.isNotEmpty()) {
                    val message = if (pendingGoals.size == 1) {
                        "You still have 1 daily goal left: ${pendingGoals[0].text}"
                    } else {
                        "You still have ${pendingGoals.size} daily goals left"
                    }
                    
                    val notification = NotificationCompat.Builder(context, "daily_summary")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Daily Summary")
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .build()
                    
                    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                    manager.notify(10000, notification)
                }
            } catch (e: Exception) {
                Log.e("DailySummaryReceiver", "Error showing daily summary: ${e.message}", e)
            }
        }
    }
}

