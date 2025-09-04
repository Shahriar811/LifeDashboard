package com.example.lifedashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lifedashboard.ui.viewmodels.AppViewModelProvider
import com.example.lifedashboard.ui.viewmodels.ExpenseViewModel
import com.example.lifedashboard.ui.viewmodels.GoalsViewModel
import com.example.lifedashboard.ui.viewmodels.TaskViewModel
import java.text.NumberFormat
import java.util.Calendar
import java.util.Currency
import java.util.Locale

@Composable
fun DashboardScreen(
    taskViewModel: TaskViewModel = viewModel(factory = AppViewModelProvider.Factory),
    expenseViewModel: ExpenseViewModel = viewModel(factory = AppViewModelProvider.Factory),
    goalsViewModel: GoalsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val tasks by taskViewModel.allTasks.collectAsState()
    val expenses by expenseViewModel.allExpenses.collectAsState()
    val goals by goalsViewModel.allGoals.collectAsState()

    val pendingTasks = tasks.count { !it.isCompleted }

    val dailyGoal = goals.filter { it.type == "Daily" }.maxByOrNull { it.creationDate }

    val today = Calendar.getInstance()
    val todaysExpenses = expenses.filter {
        val expenseDate = Calendar.getInstance().apply { timeInMillis = it.date }
        today.get(Calendar.YEAR) == expenseDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == expenseDate.get(Calendar.DAY_OF_YEAR)
    }.sumOf { it.amount }

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "BD")).apply {
        maximumFractionDigits = 2
        currency = Currency.getInstance("BDT")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Welcome Back!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Today's Summary",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                SummaryRow("Pending Tasks:", "$pendingTasks")
                SummaryRow("Spent Today:", currencyFormat.format(todaysExpenses))
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Your Daily Goal",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (dailyGoal != null) {
                    Text(
                        dailyGoal.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    GoalTimer(goal = dailyGoal)
                } else {
                    Text(
                        "No daily goal set yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}