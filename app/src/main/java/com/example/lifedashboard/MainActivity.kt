package com.example.lifedashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
// --- IMPORTANT: These are the missing import statements ---
import com.example.lifedashboard.ui.screens.DashboardScreen
import com.example.lifedashboard.ui.screens.ExpensesScreen
import com.example.lifedashboard.ui.screens.GoalsScreen
import com.example.lifedashboard.ui.screens.NotesScreen
import com.example.lifedashboard.ui.screens.TasksScreen
// ---
import com.example.lifedashboard.ui.theme.LifeDashboardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeDashboardTheme {
                AppScaffold()
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Rounded.Dashboard)
    object Tasks : Screen("tasks", "Tasks", Icons.Rounded.Done)
    object Goals : Screen("goals", "Goals", Icons.Rounded.Flag)
    object Expenses : Screen("expenses", "Expenses", Icons.Rounded.MonetizationOn)
    object Notes : Screen("notes", "Notes", Icons.Rounded.Notes)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Tasks,
    Screen.Goals,
    Screen.Expenses,
    Screen.Notes
)

@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Tasks.route) { TasksScreen() }
            composable(Screen.Goals.route) { GoalsScreen() }
            composable(Screen.Expenses.route) { ExpensesScreen() }
            composable(Screen.Notes.route) { NotesScreen() }
        }
    }
}

