Life Dashboard - Personal Productivity App
Life Dashboard is an all-in-one personal productivity and finance management Android app designed to help users take control of their daily life. With a clean and intuitive interface built with Jetpack Compose, this app brings tasks, goals, expenses, and notes together into one simple, offline-first dashboard.

✨ Features
📊 Main Dashboard: A central home screen that gives you an at-a-glance summary of your day, including:

Pending tasks.

Total money spent today.

An "Expense Breakdown" pie chart showing spending by category.

Your primary "Daily Goal" with a live countdown timer.

✅ Task Management: A complete to-do list where you can:

Add, delete, and search for tasks.

Mark tasks as complete (which grays them out with a strikethrough).

Set reminders for tasks using a date and time picker.

⏰ Task Reminders: The app schedules a precise notification using the AlarmManager for any task with a reminder set, ensuring you never miss a deadline.

🚩 Goal Tracking: Set and track your ambitions.

Create goals categorized as Daily, Weekly, or Monthly.

Each goal features a live countdown timer showing exactly how much time is left to complete it (e.g., "2h 30m left" for daily, "5d left" for weekly).

💰 Expense Tracking: A simple but powerful finance tracker.

Log expenses with a description, amount, and category (e.g., "Food").

View all expenses in a clean list, sorted by date.

Currency symbol is customizable in the settings.

📝 Notes: A simple section for jotting down thoughts, ideas, or reminders.

🔧 Settings & Customization:

Light/Dark Theme: Toggle between light and dark mode.

Currency Symbol: Set your preferred currency (e.g., BDT, $, €).

Week Start Day: Choose whether your week starts on Saturday, Sunday, or Monday to adjust goal calculations.

Data Reset: A "Clear All Data" button to wipe the app's database and start fresh.

🛠 Tech Stack & Architecture
This app is built with modern Android development practices and libraries.

100% Kotlin: The entire app is written in Kotlin.

UI: Built with Jetpack Compose and Material 3 for a declarative and modern UI.

Architecture: Follows the MVVM (Model-View-ViewModel) pattern, separating UI logic from business logic.

Database: Uses Room to provide a robust, local SQLite database for persisting all tasks, goals, expenses, and notes.

Asynchronous: Uses Kotlin Coroutines and Flow to handle data streams and background operations.

Preferences: Uses DataStore to save user settings like the theme and currency symbol.

Navigation: Uses Jetpack Navigation for Compose to manage all screen transitions.

Charts: Implements ycharts for the "Expense Breakdown" pie chart on the dashboard.

Notifications: Uses AlarmManager and BroadcastReceiver to schedule and display task reminders, even when the app is closed.

🚀 How to Build
This is a standard Android Studio project.

Clone the repository.

Open the project in Android Studio.

Build and Run the app on an Android emulator or a physical device.

Note: For task reminders to function correctly, the app will request the SCHEDULE_EXACT_ALARM and POST_NOTIFICATIONS permissions (on Android 13+).
