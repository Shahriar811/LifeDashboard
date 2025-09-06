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
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lifedashboard.ui.screens.DashboardScreen
import com.example.lifedashboard.ui.screens.ExpensesScreen
import com.example.lifedashboard.ui.screens.GoalsScreen
import com.example.lifedashboard.ui.screens.NotesScreen
import com.example.lifedashboard.ui.screens.SettingsScreen
import com.example.lifedashboard.ui.screens.TasksScreen
import com.example.lifedashboard.ui.theme.LifeDashboardTheme
import com.example.lifedashboard.ui.viewmodels.AppViewModelProvider
import com.example.lifedashboard.ui.viewmodels.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

            LifeDashboardTheme(darkTheme = isDarkTheme) {
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
    object Settings : Screen("settings", "Settings", Icons.Rounded.Settings)

}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Tasks,
    Screen.Goals,
    Screen.Expenses,
    Screen.Notes
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Life Dashboard") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
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
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}