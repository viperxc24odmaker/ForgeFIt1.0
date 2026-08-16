package com.makeforge.forgefit.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.makeforge.forgefit.ui.theme.*
import com.makeforge.forgefit.viewmodel.WorkoutSummary
import com.makeforge.forgefit.viewmodel.WorkoutViewModel

@Composable
fun WorkoutActiveScreen(
    onFinish: (WorkoutSummary) -> Unit,
    onQuit: () -> Unit,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val session by viewModel.session.collectAsState()
    val summary by viewModel.summary.collectAsState()

    LaunchedEffect(summary) {
        summary?.let(onFinish)
    }

    val currentSession = session

    // No session and no summary yet means the workout never made it across.
    if (currentSession == null) {
        if (summary == null) {
            Column(
                modifier = Modifier.fillMaxSize().background(ForgeBackground).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No active workout", style = MaterialTheme.typography.headlineMedium, color = ForgeOnSurface, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Head back and generate one to get started.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ForgeOnSurfaceDim,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onQuit,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeOrange)
                ) {
                    Text("BACK TO HOME", fontWeight = FontWeight.Black, color = ForgeBackground)
                }
            }
        }
        return
    }

    val workout = currentSession.workout
    val exercise = currentSession.currentExercise
    val isResting = currentSession.isResting
    val restLeft = currentSession.restSecondsLeft

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        // Overall progress across every set in the workout
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LinearProgressIndicator(
                progress = { currentSession.progress },
                modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = ForgeOrange,
                trackColor = ForgeSurfaceVariant
            )
            Text(
                "${currentSession.completedSets}/${workout.totalSets} sets",
                style = MaterialTheme.typography.labelLarge,
                color = ForgeOnSurfaceDim,
                fontSize = 10.sp
            )
        }

        Spacer(Modifier.height(6.dp))
        Text(
            "Exercise ${currentSession.currentExerciseIndex + 1} of ${workout.exercises.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = ForgeOnSurfaceDim,
            fontSize = 11.sp
        )

        Spacer(Modifier.height(32.dp))

        AnimatedContent(targetState = isResting, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "rest") { resting ->
            if (resting) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("REST", style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${restLeft}s",
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 80.sp),
                        color = ForgeOrange,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(12.dp))

                    val moreSets = currentSession.currentSet < exercise.sets
                    val upNext = if (moreSets) {
                        "Set ${currentSession.currentSet + 1} of ${exercise.sets}, ${exercise.name}"
                    } else {
                        workout.exercises.getOrNull(currentSession.currentExerciseIndex + 1)?.name ?: "Finish"
                    }
                    Text("UP NEXT", style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim, fontSize = 10.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        upNext,
                        style = MaterialTheme.typography.titleMedium,
                        color = ForgeOnSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(32.dp))
                    OutlinedButton(
                        onClick = { viewModel.skipRest() },
                        border = BorderStroke(1.dp, ForgeOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text("SKIP REST", color = ForgeOrange, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(exercise.muscleGroup.uppercase(), style = MaterialTheme.typography.labelLarge, color = ForgeOrange)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        exercise.name.uppercase(),
                        style = MaterialTheme.typography.displayMedium,
                        color = ForgeOnSurface,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))

                    // Which set you're on, in words
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(ForgeOrange)
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "SET ${currentSession.currentSet} OF ${exercise.sets}",
                            style = MaterialTheme.typography.titleMedium,
                            color = ForgeBackground,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // The instruction in plain English
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ForgeSurface)
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("DO THIS NOW", style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim, fontSize = 10.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "${exercise.reps} reps",
                            style = MaterialTheme.typography.displayMedium,
                            color = ForgeOrange,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Repeat the movement ${exercise.reps} times, then tap the button below.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ForgeOnSurfaceDim,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = ForgeSurfaceVariant, thickness = 0.5.dp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Then rest ${exercise.restSeconds}s before your next set.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ForgeOnSurfaceDim,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
                    }

                    if (exercise.instructions.isNotEmpty()) {
                        Spacer(Modifier.height(14.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ForgeOrange.copy(alpha = 0.10f))
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("FORM", style = MaterialTheme.typography.labelLarge, color = ForgeOrange, fontSize = 10.sp)
                            Spacer(Modifier.width(10.dp))
                            Text(exercise.instructions, style = MaterialTheme.typography.bodyMedium, color = ForgeOnSurface)
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    val isFinalSet = currentSession.currentSet >= exercise.sets &&
                        currentSession.currentExerciseIndex >= workout.exercises.size - 1

                    Button(
                        onClick = { viewModel.completeSet() },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForgeOrange)
                    ) {
                        Text(
                            if (isFinalSet) "FINISH WORKOUT" else "SET ${currentSession.currentSet} DONE",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = ForgeBackground
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    TextButton(onClick = { viewModel.quitWorkout(); onQuit() }) {
                        Text("Quit workout", color = ForgeOnSurfaceDim, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}
