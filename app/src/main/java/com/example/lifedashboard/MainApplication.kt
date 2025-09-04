package com.example.lifedashboard

import android.app.Application
import com.example.lifedashboard.data.AppDatabase
import com.example.lifedashboard.data.GoalsRepository

class MainApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val goalsRepository: GoalsRepository by lazy { GoalsRepository(this) }
}