package com.example.lifedashboard.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(context: Context) {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        val WEEK_START_DAY = stringPreferencesKey("week_start_day")
    }

    val isDarkTheme: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] ?: false
        }

    val currencySymbol: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.CURRENCY_SYMBOL] ?: "BDT"
    }

    val weekStartDay: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.WEEK_START_DAY] ?: "Saturday"
    }

    suspend fun setDarkTheme(isDarkTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] = isDarkTheme
        }
    }

    suspend fun setCurrencySymbol(symbol: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENCY_SYMBOL] = symbol
        }
    }

    suspend fun setWeekStartDay(day: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.WEEK_START_DAY] = day
        }
    }
}