package com.example.lifedashboard.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifedashboard.TaskNotificationScheduler
import com.example.lifedashboard.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

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

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    val filteredTasks: StateFlow<List<Task>> = combine(
        allTasks,
        selectedCategory
    ) { tasks, category ->
        if (category == null) tasks else tasks.filter { it.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    fun insertTask(text: String, dueDate: Long? = null, category: String = "Personal", priority: String = "Medium") = viewModelScope.launch {
        val task = Task(text = text, dueDate = dueDate, category = category, priority = priority)
        taskDao.insertTask(task)
        // Get the inserted task with its ID to schedule notification
        // Small delay to ensure database transaction has committed
        if (dueDate != null && dueDate > System.currentTimeMillis()) {
            delay(100) // Wait 100ms for database to commit
            val insertedTask = taskDao.getLatestTaskByTextAndDate(text, dueDate)
            if (insertedTask != null && insertedTask.id > 0) {
                scheduler.schedule(insertedTask)
            } else {
                // Fallback: Try getting from all tasks flow
                taskDao.getAllTasks().first().firstOrNull { 
                    it.text == text && it.dueDate == dueDate && it.id > 0 
                }?.let {
                    scheduler.schedule(it)
                }
            }
        }
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filteredNotes: StateFlow<List<Note>> = combine(
        allNotes,
        _searchQuery
    ) { notes, query ->
        if (query.isBlank()) {
            notes
        } else {
            notes.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.content.contains(query, ignoreCase = true) 
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun insertNote(title: String, content: String, colorLabel: String = "None") = viewModelScope.launch {
        noteDao.insertNote(Note(title = title, content = content, colorLabel = colorLabel))
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        noteDao.updateNote(note)
    }

    fun togglePin(note: Note) = viewModelScope.launch {
        noteDao.updateNote(note.copy(isPinned = !note.isPinned))
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