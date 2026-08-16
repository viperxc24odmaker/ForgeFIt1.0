package com.makeforge.forgefit.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.makeforge.forgefit.domain.model.Jog
import com.makeforge.forgefit.domain.model.formatDuration
import com.makeforge.forgefit.ui.theme.*
import com.makeforge.forgefit.viewmodel.JogPhase
import com.makeforge.forgefit.viewmodel.JogViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun JogScreen(viewModel: JogViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(56.dp))

        Text("JOG TRACKER", style = MaterialTheme.typography.labelLarge, color = ForgeOrange)
        Text(
            "MORNING RUN",
            style = MaterialTheme.typography.displayMedium,
            color = ForgeOnSurface,
            fontWeight = FontWeight.Black
        )

        Spacer(Modifier.height(20.dp))

        // Lifetime stats
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            JogStat(Modifier.weight(1f), "🏃", "${state.totalJogs}", "JOGS")
            JogStat(Modifier.weight(1f), "📏", String.format(Locale.US, "%.1f", state.totalDistanceKm), "TOTAL KM")
            JogStat(
                Modifier.weight(1f),
                "⚡",
                state.bestPaceSecondsPerKm?.let { "${it / 60}:${(it % 60).toString().padStart(2, '0')}" } ?: "--:--",
                "BEST PACE"
            )
        }

        Spacer(Modifier.height(28.dp))

        state.savedMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ForgeSuccess.copy(alpha = 0.14f))
                    .border(1.dp, ForgeSuccess.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { viewModel.dismissMessage() }
                    .padding(14.dp)
            ) {
                Text(msg, style = MaterialTheme.typography.bodyMedium, color = ForgeSuccess)
            }
            Spacer(Modifier.height(16.dp))
        }

        AnimatedContent(targetState = state.phase, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "jog") { phase ->
            when (phase) {
                JogPhase.IDLE -> IdleView(onStart = { viewModel.start() })

                JogPhase.RUNNING, JogPhase.PAUSED -> RunningView(
                    elapsed = state.elapsedSeconds,
                    isPaused = phase == JogPhase.PAUSED,
                    onPause = { viewModel.pause() },
                    onResume = { viewModel.resume() },
                    onFinish = { viewModel.finish() },
                    onCancel = { viewModel.cancel() }
                )

                JogPhase.ENTERING_DISTANCE -> DistanceEntryView(
                    elapsed = state.elapsedSeconds,
                    distanceInput = state.distanceInput,
                    livePaceSeconds = state.livePaceSeconds,
                    onDistanceChange = viewModel::onDistanceChange,
                    onSave = { viewModel.saveJog() },
                    onDiscard = { viewModel.cancel() }
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        if (state.history.isNotEmpty()) {
            Text("RECENT JOGS", style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim)
            Spacer(Modifier.height(12.dp))
            state.history.take(10).forEach { jog ->
                JogHistoryRow(jog)
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun IdleView(onStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(ForgeOrange)
                .clickable { onStart() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🏃", fontSize = 40.sp)
                Spacer(Modifier.height(4.dp))
                Text("START", style = MaterialTheme.typography.headlineMedium, color = ForgeBackground, fontWeight = FontWeight.Black)
                Text("JOG", style = MaterialTheme.typography.titleMedium, color = ForgeBackground, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "Tap to start the timer. Pause any time. When you get back, enter your distance and we work out your pace and calories.",
            style = MaterialTheme.typography.bodyMedium,
            color = ForgeOnSurfaceDim,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RunningView(
    elapsed: Int,
    isPaused: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onFinish: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if (isPaused) "PAUSED" else "RUNNING",
            style = MaterialTheme.typography.labelLarge,
            color = if (isPaused) ForgeOnSurfaceDim else ForgeOrange
        )
        Spacer(Modifier.height(10.dp))
        Text(
            formatDuration(elapsed),
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 68.sp),
            color = ForgeOnSurface,
            fontWeight = FontWeight.Black
        )
        Spacer(Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { if (isPaused) onResume() else onPause() },
                modifier = Modifier.weight(1f).height(56.dp),
                border = BorderStroke(1.dp, ForgeOrange),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(if (isPaused) "RESUME" else "PAUSE", color = ForgeOrange, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onFinish,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForgeOrange)
            ) {
                Text("FINISH", color = ForgeBackground, fontWeight = FontWeight.Black)
            }
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onCancel) {
            Text("Cancel jog", color = ForgeOnSurfaceDim, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun DistanceEntryView(
    elapsed: Int,
    distanceInput: String,
    livePaceSeconds: Int?,
    onDistanceChange: (String) -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("NICE ONE 🔥", style = MaterialTheme.typography.headlineMedium, color = ForgeOrange, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(6.dp))
        Text("You jogged for ${formatDuration(elapsed)}", style = MaterialTheme.typography.bodyLarge, color = ForgeOnSurface)
        Spacer(Modifier.height(24.dp))

        Text("How far did you go?", style = MaterialTheme.typography.titleMedium, color = ForgeOnSurface, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = distanceInput,
            onValueChange = onDistanceChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("e.g. 3.5", color = ForgeOnSurfaceDim) },
            suffix = { Text("km", color = ForgeOnSurfaceDim) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForgeOrange,
                unfocusedBorderColor = ForgeSurfaceVariant,
                focusedTextColor = ForgeOnSurface,
                unfocusedTextColor = ForgeOnSurface,
                cursorColor = ForgeOrange
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("1", "2", "3", "5").forEach { preset ->
                OutlinedButton(
                    onClick = { onDistanceChange(preset) },
                    border = BorderStroke(1.dp, ForgeSurfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("$preset km", color = ForgeOnSurfaceDim, fontSize = 12.sp)
                }
            }
        }

        livePaceSeconds?.let { pace ->
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ForgeSurface)
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${pace / 60}:${(pace % 60).toString().padStart(2, '0')}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = ForgeOrange,
                            fontWeight = FontWeight.Black
                        )
                        Text("MIN / KM", style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim, fontSize = 9.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            formatDuration(elapsed),
                            style = MaterialTheme.typography.headlineMedium,
                            color = ForgeOnSurface,
                            fontWeight = FontWeight.Black
                        )
                        Text("TIME", style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim, fontSize = 9.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ForgeOrange)
        ) {
            Text("SAVE JOG 💪", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = ForgeBackground)
        }
        TextButton(onClick = onDiscard) {
            Text("Discard", color = ForgeOnSurfaceDim, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun JogStat(modifier: Modifier = Modifier, icon: String, value: String, label: String) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(ForgeSurface).padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.height(6.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, color = ForgeOnSurface, fontWeight = FontWeight.Black)
        Text(label, style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim, fontSize = 9.sp)
    }
}

@Composable
private fun JogHistoryRow(jog: Jog) {
    val fmt = remember { SimpleDateFormat("EEE d MMM", Locale.getDefault()) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ForgeSurface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                String.format(Locale.US, "%.2f km", jog.distanceKm),
                style = MaterialTheme.typography.titleMedium,
                color = ForgeOnSurface,
                fontWeight = FontWeight.Bold
            )
            Text(fmt.format(jog.date), style = MaterialTheme.typography.bodyMedium, color = ForgeOnSurfaceDim, fontSize = 11.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(jog.durationFormatted, style = MaterialTheme.typography.titleMedium, color = ForgeOrange, fontWeight = FontWeight.Bold)
            Text("${jog.paceFormatted} /km  ·  ${jog.caloriesBurned} kcal", style = MaterialTheme.typography.bodyMedium, color = ForgeOnSurfaceDim, fontSize = 11.sp)
        }
    }
}
