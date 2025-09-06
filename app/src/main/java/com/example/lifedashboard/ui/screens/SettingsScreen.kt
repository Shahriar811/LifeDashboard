package com.example.lifedashboard.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lifedashboard.ui.viewmodels.AppViewModelProvider
import com.example.lifedashboard.ui.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
    val currencySymbol by settingsViewModel.currencySymbol.collectAsState()
    val weekStartDay by settingsViewModel.weekStartDay.collectAsState()

    var showCreditsDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var tempCurrency by remember { mutableStateOf(currencySymbol) }

    LaunchedEffect(currencySymbol) {
        tempCurrency = currencySymbol
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Theme", modifier = Modifier.weight(1f))
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { settingsViewModel.setDarkTheme(it) }
            )
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        OutlinedTextField(
            value = tempCurrency,
            onValueChange = { tempCurrency = it },
            label = { Text("Currency Symbol (e.g., $, BDT, €)") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                settingsViewModel.setCurrencySymbol(tempCurrency)
                Toast.makeText(context, "Currency saved", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp)
        ) {
            Text("Save Currency")
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        WeekStartDaySelector(
            selectedDay = weekStartDay,
            onDaySelected = { settingsViewModel.setWeekStartDay(it) }
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Button(onClick = { showCreditsDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Credits")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showResetDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Clear All Data")
        }
    }

    if (showCreditsDialog) {
        AlertDialog(
            onDismissRequest = { showCreditsDialog = false },
            title = { Text("Credits") },
            text = {
                Column {
                    Text("Developer: Md. Shahriar Hossain")
                    Text("Email: mdshahriarhossain08@gmail.com")
                }
            },
            confirmButton = {
                Button(onClick = { showCreditsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Confirm Reset") },
            text = { Text("Are you sure you want to delete all data? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        settingsViewModel.clearAllData()
                        Toast.makeText(context, "All data cleared", Toast.LENGTH_SHORT).show()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekStartDaySelector(selectedDay: String, onDaySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val days = listOf("Saturday", "Sunday", "Monday")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedDay,
            onValueChange = {},
            readOnly = true,
            label = { Text("Week Starts On") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            days.forEach { day ->
                DropdownMenuItem(
                    text = { Text(day) },
                    onClick = {
                        onDaySelected(day)
                        expanded = false
                    }
                )
            }
        }
    }
}