package com.makeforge.forgefit.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.makeforge.forgefit.ui.theme.*
import com.makeforge.forgefit.viewmodel.HomeViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    onStartWorkout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Live clock
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            currentTime = getCurrentTime()
        }
    }

    val isFirstTime = state.profile.totalSessions == 0
    val greeting = getGreeting()
    val name = state.profile.name.ifEmpty { "Soldier" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(56.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                // First time = Hello, returning = Welcome Back
                Text(
                    text = if (isFirstTime) "HELLO," else "WELCOME BACK,",
                    style = MaterialTheme.typography.labelLarge,
                    color = ForgeOrange
                )
                Text(
                    text = name.uppercase(),
                    style = MaterialTheme.typography.displayMedium,
                    color = ForgeOnSurface,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(4.dp))
                // Greeting + live time
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ForgeOnSurfaceDim
                    )
                    Text("·", color = ForgeOnSurfaceDim)
                    Text(
                        text = currentTime,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ForgeOnSurfaceDim,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Jacked Score Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ForgeSurfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔥", fontSize = 18.sp)
                    Text(
                        text = "${state.profile.jackedScore}",
                        style = MaterialTheme.typography.titleLarge,
                        color = ForgeOrange,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "JACKED",
                        style = MaterialTheme.typography.labelLarge,
                        color = ForgeOnSurfaceDim,
                        fontSize = 9.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // First time welcome banner
        if (isFirstTime) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(ForgeOrange.copy(alpha = 0.2f), ForgeSurfaceVariant)
                        )
                    )
                    .border(1.dp, ForgeOrange.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "👋 First time here, $name!",
                        style = MaterialTheme.typography.titleMedium,
                        color = ForgeOrange,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Generate your first workout below and start your journey to getting JACKED. Let's go 💪",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ForgeOnSurface
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Motivational message card
        if (state.motivationalMessage.isNotEmpty() && !isFirstTime) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(ForgeOrangeDim.copy(alpha = 0.3f), ForgeSurfaceVariant)
                        )
                    )
                    .border(1.dp, ForgeOrange.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Text(
                    text = "\"${state.motivationalMessage}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ForgeOnSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(modifier = Modifier.weight(1f), label = "STREAK", value = "${state.profile.currentStreak}d", icon = "🔥")
            StatCard(modifier = Modifier.weight(1f), label = "SESSIONS", value = "${state.profile.totalSessions}", icon = "💪")
            StatCard(modifier = Modifier.weight(1f), label = "GOAL", value = state.profile.goal.displayName.split(" ").first().uppercase(), icon = "🎯")
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "TODAY'S GRIND",
            style = MaterialTheme.typography.labelLarge,
            color = ForgeOnSurfaceDim
        )

        Spacer(Modifier.height(12.dp))

        if (state.todayWorkout == null) {
            Button(
                onClick = { viewModel.generateTodayWorkout() },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForgeOrange),
                enabled = !state.isGeneratingWorkout
            ) {
                if (state.isGeneratingWorkout) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = ForgeBackground, strokeWidth = 2.dp)
                    Spacer(Modifier.width(12.dp))
                    Text("AI IS COOKING...", fontWeight = FontWeight.Black, color = ForgeBackground)
                } else {
                    Icon(Icons.Default.FitnessCenter, null, tint = ForgeBackground)
                    Spacer(Modifier.width(10.dp))
                    Text("GENERATE WORKOUT", fontWeight = FontWeight.Black, color = ForgeBackground)
                }
            }
        } else {
            val workout = state.todayWorkout!!
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(ForgeSurface)
                    .border(1.dp, ForgeSurfaceVariant, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(workout.title, style = MaterialTheme.typography.headlineMedium, color = ForgeOrange, fontWeight = FontWeight.Black)
                    Text("${workout.durationMinutes}min", style = MaterialTheme.typography.bodyMedium, color = ForgeOnSurfaceDim)
                }

                Spacer(Modifier.height(16.dp))

                workout.exercises.take(4).forEach { exercise ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(exercise.name, style = MaterialTheme.typography.bodyMedium, color = ForgeOnSurface)
                        Text("${exercise.sets}×${exercise.reps}", style = MaterialTheme.typography.bodyMedium, color = ForgeOrange, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = ForgeSurfaceVariant, thickness = 0.5.dp)
                }

                if (workout.exercises.size > 4) {
                    Text("+${workout.exercises.size - 4} more exercises", style = MaterialTheme.typography.bodyMedium, color = ForgeOnSurfaceDim, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = onStartWorkout,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeOrange)
                ) {
                    Text("LET'S GO 💪", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = ForgeBackground)
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, label: String, value: String, icon: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(ForgeSurface)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 20.sp)
        Spacer(Modifier.height(6.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, color = ForgeOnSurface, fontWeight = FontWeight.Black)
        Text(label, style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim, fontSize = 9.sp)
    }
}

private fun getCurrentTime(): String {
    val cal = Calendar.getInstance()
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)
    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "$displayHour:${minute.toString().padStart(2, '0')} $amPm"
}

private fun getGreeting(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
}
