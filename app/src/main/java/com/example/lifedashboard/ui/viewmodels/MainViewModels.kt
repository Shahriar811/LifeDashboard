package com.example.lifedashboard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifedashboard.data.Expense
import com.example.lifedashboard.data.ExpenseDao
import com.example.lifedashboard.data.Goal
import com.example.lifedashboard.data.GoalDao
import com.example.lifedashboard.data.Note
import com.example.lifedashboard.data.NoteDao
import com.example.lifedashboard.data.Task
import com.example.lifedashboard.data.TaskDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {
    val allTasks: StateFlow<List<Task>> = taskDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertTask(text: String) = viewModelScope.launch {
        taskDao.insertTask(Task(text = text))
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        taskDao.deleteTask(task)
    }
}

class ExpenseViewModel(private val expenseDao: ExpenseDao) : ViewModel() {
    val allExpenses: StateFlow<List<Expense>> = expenseDao.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertExpense(description: String, amount: Double, category: String) = viewModelScope.launch {
        expenseDao.insertExpense(Expense(description = description, amount = amount, category = category))
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        expenseDao.deleteExpense(expense)
    }
}

class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {
    val allNotes: StateFlow<List<Note>> = noteDao.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertNote(title: String, content: String) = viewModelScope.launch {
        noteDao.insertNote(Note(title = title, content = content))
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        noteDao.deleteNote(note)
    }
}

class GoalsViewModel(private val goalDao: GoalDao) : ViewModel() {
    val allGoals: StateFlow<List<Goal>> = goalDao.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertGoal(text: String, type: String) = viewModelScope.launch {
        goalDao.insertGoal(Goal(text = text, type = type))
    }

    fun deleteGoal(goal: Goal) = viewModelScope.launch {
        goalDao.deleteGoal(goal)
    }
}