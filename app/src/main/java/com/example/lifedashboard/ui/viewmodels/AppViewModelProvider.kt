package com.example.lifedashboard.ui.viewmodels

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lifedashboard.MainApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for TaskViewModel
        initializer {
            TaskViewModel(
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication).database.taskDao()
            )
        }
        // Initializer for ExpenseViewModel
        initializer {
            ExpenseViewModel(
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication).database.expenseDao()
            )
        }
        // Initializer for NoteViewModel
        initializer {
            NoteViewModel(
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication).database.noteDao()
            )
        }
        // Initializer for GoalsViewModel - THIS IS THE CORRECTED LINE
        initializer {
            GoalsViewModel(
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication).database.goalDao()
            )
        }
    }
}

// A helper function to easily get a reference to the Application instance
fun CreationExtras.mainApplication(): MainApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)

