package com.example.lifedashboard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.lifedashboard.data.AppDatabase
import com.example.lifedashboard.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val db: AppDatabase
) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean> = userPreferencesRepository.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    val currencySymbol: StateFlow<String> = userPreferencesRepository.currencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "BDT"
        )

    val weekStartDay: StateFlow<String> = userPreferencesRepository.weekStartDay
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "Saturday"
        )

    fun setDarkTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setDarkTheme(isDarkTheme)
        }
    }

    fun setCurrencySymbol(symbol: String) {
        viewModelScope.launch {
            userPreferencesRepository.setCurrencySymbol(symbol)
        }
    }

    fun setWeekStartDay(day: String) {
        viewModelScope.launch {
            userPreferencesRepository.setWeekStartDay(day)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            db.withTransaction {
                db.taskDao().clear()
                db.expenseDao().clear()
                db.noteDao().clear()
                db.goalDao().clear()
            }
        }
    }
}