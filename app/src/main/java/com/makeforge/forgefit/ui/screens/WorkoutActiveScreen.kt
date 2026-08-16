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
import com.makeforge.forgefit.viewmodel.WorkoutViewModel

@Composable
fun WorkoutActiveScreen(
    onFinish: (exerciseCount: Int) -> Unit,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val session by viewModel.session.collectAsState()
    val isFinished by viewModel.isFinished.collectAsState()

    LaunchedEffect(isFinished) {
        if (isFinished) {
            onFinish(session?.workout?.exercises?.size ?: 0)
        }
    }

    val currentSession = session ?: return

    val workout = currentSession.workout
    val currentIndex = currentSession.currentExerciseIndex
    val isResting = currentSession.isResting
    val restLeft = currentSession.restSecondsLeft
    val exercise = workout.exercises[currentIndex]
    val progress = (currentIndex + 1).toFloat() / workout.exercises.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBackground)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(56.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = ForgeOrange,
                trackColor = ForgeSurfaceVariant
            )
            Text("${currentIndex + 1}/${workout.exercises.size}", style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim)
        }

        Spacer(Modifier.height(40.dp))

        AnimatedContent(targetState = isResting, transitionSpec = { fadeIn() togetherWith fadeOut() }) { resting ->
            if (resting) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("REST", style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "${restLeft}s",
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 80.sp),
                        color = ForgeOrange,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(8.dp))
                    val nextName = workout.exercises.getOrNull(currentIndex + 1)?.name ?: "FINISH 🔥"
                    Text("NEXT: $nextName", style = MaterialTheme.typography.bodyMedium, color = ForgeOnSurfaceDim)
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
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = exercise.name.uppercase(),
                        style = MaterialTheme.typography.displayMedium,
                        color = ForgeOnSurface,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(32.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${exercise.sets}", style = MaterialTheme.typography.displayMedium, color = ForgeOnSurface, fontWeight = FontWeight.Black)
                            Text("SETS", style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim)
                        }
                        Box(Modifier.width(1.dp).height(60.dp).background(ForgeSurfaceVariant))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(exercise.reps, style = MaterialTheme.typography.displayMedium, color = ForgeOnSurface, fontWeight = FontWeight.Black)
                            Text("REPS", style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim)
                        }
                    }
                    if (exercise.instructions.isNotEmpty()) {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = exercise.instructions,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ForgeOnSurfaceDim,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(ForgeSurface).padding(16.dp)
                        )
                    }
                    Spacer(Modifier.height(48.dp))
                    Button(
                        onClick = { viewModel.completeExercise() },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForgeOrange)
                    ) {
                        Text("SET DONE ✓", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = ForgeBackground)
                    }
                }
            }
        }
    }
}
