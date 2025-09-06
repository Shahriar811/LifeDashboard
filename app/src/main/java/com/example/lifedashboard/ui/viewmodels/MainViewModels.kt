package com.example.lifedashboard.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifedashboard.TaskNotificationScheduler
import com.example.lifedashboard.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// This class definition fixes the "Unresolved reference" error
class TaskViewModel(application: Application, private val taskDao: TaskDao) : AndroidViewModel(application) {
    private val scheduler = TaskNotificationScheduler(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val allTasks: StateFlow<List<Task>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                taskDao.getAllTasks()
            } else {
                taskDao.getTasksFiltered("%$query%")
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun insertTask(text: String) = viewModelScope.launch {
        taskDao.insertTask(Task(text = text))
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.updateTask(task)
        if (task.dueDate != null && !task.isCompleted) {
            scheduler.schedule(task)
        } else {
            scheduler.cancel(task)
        }
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        scheduler.cancel(task)
        taskDao.deleteTask(task)
    }
}

class ExpenseViewModel(private val expenseDao: ExpenseDao) : AndroidViewModel(Application()) {
    val allExpenses: StateFlow<List<Expense>> = expenseDao.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertExpense(description: String, amount: Double, category: String) = viewModelScope.launch {
        expenseDao.insertExpense(Expense(description = description, amount = amount, category = category))
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        expenseDao.deleteExpense(expense)
    }
}

class NoteViewModel(private val noteDao: NoteDao) : AndroidViewModel(Application()) {
    val allNotes: StateFlow<List<Note>> = noteDao.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertNote(title: String, content: String) = viewModelScope.launch {
        noteDao.insertNote(Note(title = title, content = content))
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        noteDao.deleteNote(note)
    }
}

class GoalsViewModel(private val goalDao: GoalDao) : AndroidViewModel(Application()) {
    val allGoals: StateFlow<List<Goal>> = goalDao.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertGoal(text: String, type: String) = viewModelScope.launch {
        goalDao.insertGoal(Goal(text = text, type = type))
    }

    fun deleteGoal(goal: Goal) = viewModelScope.launch {
        goalDao.deleteGoal(goal)
    }
}