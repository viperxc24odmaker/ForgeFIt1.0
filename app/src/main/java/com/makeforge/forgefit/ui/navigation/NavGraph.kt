package com.makeforge.forgefit.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.makeforge.forgefit.ui.screens.*
import com.makeforge.forgefit.ui.theme.*
import com.makeforge.forgefit.viewmodel.MainViewModel
import com.makeforge.forgefit.viewmodel.OnboardingViewModel

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object Jog : Screen("jog")
    data object WorkoutActive : Screen("workout_active")
    data object WorkoutComplete : Screen("workout_complete/{exercises}/{sets}/{points}/{minutes}") {
        fun createRoute(exercises: Int, sets: Int, points: Int, minutes: Int) =
            "workout_complete/$exercises/$sets/$points/$minutes"
    }
}

private data class TabItem(val screen: Screen, val label: String, val icon: ImageVector)

private val tabs = listOf(
    TabItem(Screen.Home, "Workout", Icons.Default.FitnessCenter),
    TabItem(Screen.Jog, "Jog", Icons.Default.DirectionsRun)
)

@Composable
fun ForgeFitNavGraph(navController: NavHostController) {
    val mainViewModel: MainViewModel = hiltViewModel()
    val isOnboarded by mainViewModel.isOnboarded.collectAsState()

    if (isOnboarded == null) return

    val startDestination = if (isOnboarded == true) Screen.Home.route else Screen.Onboarding.route

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute == Screen.Home.route || currentRoute == Screen.Jog.route

    Scaffold(
        containerColor = ForgeBackground,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = ForgeSurface) {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.screen.route,
                            onClick = {
                                if (currentRoute != tab.screen.route) {
                                    navController.navigate(tab.screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ForgeBackground,
                                selectedTextColor = ForgeOrange,
                                indicatorColor = ForgeOrange,
                                unselectedIconColor = ForgeOnSurfaceDim,
                                unselectedTextColor = ForgeOnSurfaceDim
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
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
                HomeScreen(
                    onStartWorkout = { navController.navigate(Screen.WorkoutActive.route) }
                )
            }

            composable(Screen.Jog.route) {
                JogScreen()
            }

            composable(Screen.WorkoutActive.route) {
                WorkoutActiveScreen(
                    onFinish = { summary ->
                        navController.navigate(
                            Screen.WorkoutComplete.createRoute(
                                summary.exerciseCount,
                                summary.totalSets,
                                summary.jackedPoints,
                                summary.elapsedMinutes
                            )
                        ) {
                            popUpTo(Screen.WorkoutActive.route) { inclusive = true }
                        }
                    },
                    onQuit = {
                        navController.popBackStack(Screen.Home.route, inclusive = false)
                    }
                )
            }

            composable(Screen.WorkoutComplete.route) { entry ->
                val args = entry.arguments
                WorkoutCompleteScreen(
                    exerciseCount = args?.getString("exercises")?.toIntOrNull() ?: 0,
                    totalSets = args?.getString("sets")?.toIntOrNull() ?: 0,
                    jackedPoints = args?.getString("points")?.toIntOrNull() ?: 0,
                    elapsedMinutes = args?.getString("minutes")?.toIntOrNull() ?: 0,
                    onHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
