package com.example.lifedashboard.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lifedashboard.data.Goal
import com.example.lifedashboard.ui.viewmodels.AppViewModelProvider
import com.example.lifedashboard.ui.viewmodels.GoalsViewModel
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val goals by viewModel.allGoals.collectAsState()

    val groupedGoals = goals.groupBy { it.type }
        .toSortedMap(compareBy { goalType ->
            when (goalType) {
                "Daily" -> 0
                "Monthly" -> 1
                else -> 2
            }
        })

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "Your Goals",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (goals.isEmpty()) {
                    item {
                        Text(
                            "No goals set yet. Tap the '+' button to add one!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }
                } else {
                    groupedGoals.forEach { (type, goalsInType) ->
                        stickyHeader {
                            Surface(color = MaterialTheme.colorScheme.background) {
                                Text(
                                    text = type,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        items(goalsInType, key = { it.id }) { goal ->
                            GoalItem(goal = goal, onDelete = { viewModel.deleteGoal(it) })
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddGoalDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { text, type ->
                    viewModel.insertGoal(text, type)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun GoalItem(goal: Goal, onDelete: (Goal) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Countdown Timer Display
                GoalTimer(goal = goal)
            }
            IconButton(onClick = { onDelete(goal) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Goal",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun GoalTimer(goal: Goal) {
    var timeLeft by remember { mutableStateOf("") }

    LaunchedEffect(key1 = goal) {
        while (true) {
            val now = Calendar.getInstance()
            val goalCalendar = Calendar.getInstance().apply { timeInMillis = goal.creationDate }

            val endOfPeriod = when (goal.type) {
                "Daily" -> (goalCalendar.clone() as Calendar).apply {
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }
                "Monthly" -> (goalCalendar.clone() as Calendar).apply {
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }
                else -> now // Should not happen
            }

            val diff = endOfPeriod.timeInMillis - now.timeInMillis

            timeLeft = if (diff > 0) {
                if (goal.type == "Daily") {
                    val hours = TimeUnit.MILLISECONDS.toHours(diff)
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
                    "${hours}h ${minutes}m left"
                } else { // Monthly
                    val days = TimeUnit.MILLISECONDS.toDays(diff)
                    if (days > 0) "${days}d left" else "Last day!"
                }
            } else {
                "Time's up!"
            }
            delay(60000) // Update every minute
        }
    }

    if (timeLeft.isNotEmpty()) {
        Text(
            text = timeLeft,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    // Updated list of goal types
    val goalTypes = listOf("Daily", "Monthly")
    var selectedType by remember { mutableStateOf(goalTypes[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a New Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Goal Description") }
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Goal Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        goalTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onAdd(text, selectedType)
                    }
                },
                enabled = text.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}