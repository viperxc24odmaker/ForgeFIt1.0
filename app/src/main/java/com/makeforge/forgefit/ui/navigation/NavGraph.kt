package com.makeforge.forgefit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.makeforge.forgefit.ui.screens.HomeScreen
import com.makeforge.forgefit.ui.screens.WorkoutActiveScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object WorkoutActive : Screen("workout_active")
}

@Composable
fun ForgeFitNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onStartWorkout = { navController.navigate(Screen.WorkoutActive.route) }
            )
        }
        composable(Screen.WorkoutActive.route) {
            WorkoutActiveScreen(
                onFinish = { navController.popBackStack() }
            )
        }
    }
}
