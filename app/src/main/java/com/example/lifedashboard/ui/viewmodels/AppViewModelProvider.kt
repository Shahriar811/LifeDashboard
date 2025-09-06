package com.example.lifedashboard.ui.viewmodels

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lifedashboard.MainApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            TaskViewModel(
                mainApplication(),
                mainApplication().database.taskDao()
            )
        }
        initializer {
            ExpenseViewModel(
                mainApplication().database.expenseDao()
            )
        }
        initializer {
            NoteViewModel(
                mainApplication().database.noteDao()
            )
        }
        initializer {
            GoalsViewModel(
                mainApplication().database.goalDao()
            )
        }
        initializer {
            SettingsViewModel(
                mainApplication().userPreferencesRepository,
                mainApplication().database
            )
        }
    }
}

fun CreationExtras.mainApplication(): MainApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)