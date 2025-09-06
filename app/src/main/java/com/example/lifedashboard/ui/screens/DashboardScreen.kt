package com.example.lifedashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.lifedashboard.ui.viewmodels.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

@Composable
fun DashboardScreen(
    taskViewModel: TaskViewModel = viewModel(factory = AppViewModelProvider.Factory),
    expenseViewModel: ExpenseViewModel = viewModel(factory = AppViewModelProvider.Factory),
    goalsViewModel: GoalsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val tasks by taskViewModel.allTasks.collectAsState()
    val expenses by expenseViewModel.allExpenses.collectAsState()
    val goals by goalsViewModel.allGoals.collectAsState()
    val currencySymbol by settingsViewModel.currencySymbol.collectAsState()

    val pendingTasks = tasks.count { !it.isCompleted }

    val dailyGoal = goals.filter { it.type == "Daily" }.maxByOrNull { it.creationDate }

    val today = Calendar.getInstance()
    val todaysExpenses = expenses.filter {
        val expenseDate = Calendar.getInstance().apply { timeInMillis = it.date }
        today.get(Calendar.YEAR) == expenseDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == expenseDate.get(Calendar.DAY_OF_YEAR)
    }.sumOf { it.amount }

    val currencyFormat = (NumberFormat.getCurrencyInstance() as DecimalFormat).apply {
        maximumFractionDigits = 2
        val symbols = this.decimalFormatSymbols
        symbols.currencySymbol = currencySymbol
        this.decimalFormatSymbols = symbols
    }

    val expenseByCategory = expenses
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    val pieChartData = PieChartData(
        slices = expenseByCategory.map { (category, sum) ->
            PieChartData.Slice(
                label = category,
                value = sum.toFloat(),
                color = Color((0..255).random(), (0..255).random(), (0..255).random())
            )
        },
        plotType = PlotType.Pie
    )

    val pieChartConfig = PieChartConfig(
        isAnimationEnable = true,
        showSliceLabels = true,
        sliceLabelTextSize = 12.sp,
        sliceLabelTextColor = Color.White,
        backgroundColor = MaterialTheme.colorScheme.surface
    )


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

        if (pieChartData.slices.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Expense Breakdown",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PieChart(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .aspectRatio(1f)
                            .padding(16.dp),
                        pieChartData = pieChartData,
                        pieChartConfig = pieChartConfig
                    )
                }
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