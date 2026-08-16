package com.makeforge.forgefit.ui.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.makeforge.forgefit.ui.screens.*
import com.makeforge.forgefit.viewmodel.MainViewModel
import com.makeforge.forgefit.viewmodel.OnboardingViewModel
import com.makeforge.forgefit.viewmodel.WorkoutViewModel

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object WorkoutActive : Screen("workout_active")
    object WorkoutComplete : Screen("workout_complete/{exerciseCount}") {
        fun createRoute(count: Int) = "workout_complete/$count"
    }
}

@Composable
fun ForgeFitNavGraph(navController: NavHostController) {
    val mainViewModel: MainViewModel = hiltViewModel()
    val isOnboarded by mainViewModel.isOnboarded.collectAsState()

    // Wait until we know onboarding state
    if (isOnboarded == null) return

    val startDestination = if (isOnboarded == true) Screen.Home.route else Screen.Onboarding.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Onboarding.route) {
            val viewModel: OnboardingViewModel = hiltViewModel()
            OnboardingScreen(onComplete = { profile ->
                viewModel.saveProfile(profile)
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            val workoutViewModel: WorkoutViewModel = hiltViewModel()
            HomeScreen(
                onStartWorkout = { workout ->
                    workoutViewModel.startWorkout(workout)
                    navController.navigate(Screen.WorkoutActive.route)
                }
            )
        }

        composable(Screen.WorkoutActive.route) {
            WorkoutActiveScreen(
                onFinish = { exerciseCount ->
                    navController.navigate(Screen.WorkoutComplete.createRoute(exerciseCount)) {
                        popUpTo(Screen.WorkoutActive.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.WorkoutComplete.route) { backStackEntry ->
            val exerciseCount = backStackEntry.arguments?.getString("exerciseCount")?.toIntOrNull() ?: 0
            WorkoutCompleteScreen(
                exerciseCount = exerciseCount,
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
