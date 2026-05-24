package com.example.lifedashboard.data

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GoalsRepository(context: Context) {
    private val prefs = context.getSharedPreferences("goals_prefs", Context.MODE_PRIVATE)

    private val _goals = MutableStateFlow(loadGoals())
    val goals: StateFlow<Map<String, String>> = _goals

    private fun loadGoals(): Map<String, String> {
        return mapOf(
            "daily" to prefs.getString("daily", "")!!,
            "weekly" to prefs.getString("weekly", "")!!,
            "monthly" to prefs.getString("monthly", "")!!,
            "yearly" to prefs.getString("yearly", "")!!
        )
    }

    fun saveGoals(daily: String, weekly: String, monthly: String, yearly: String) {
        prefs.edit {
            putString("daily", daily)
            putString("weekly", weekly)
            putString("monthly", monthly)
            putString("yearly", yearly)
        }
        _goals.value = loadGoals()
    }
}
