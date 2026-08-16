package com.makeforge.forgefit.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makeforge.forgefit.domain.model.*
import com.makeforge.forgefit.ui.theme.*

@Composable
fun OnboardingScreen(onComplete: (UserProfile) -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weightKg by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf(FitnessGoal.BUILD_MUSCLE) }
    var fitnessLevel by remember { mutableStateOf(FitnessLevel.BEGINNER) }
    var selectedEquipment by remember { mutableStateOf(setOf<Equipment>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(64.dp))

        // Progress dots
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(4) { i ->
                Box(
                    modifier = Modifier
                        .size(if (i == step) 24.dp else 8.dp, 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (i == step) ForgeOrange else ForgeSurfaceVariant)
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        AnimatedContent(targetState = step, transitionSpec = {
            slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
        }) { currentStep ->
            when (currentStep) {
                0 -> StepName(name = name, onNameChange = { name = it }) {
                    if (name.isNotBlank()) step = 1
                }
                1 -> StepBody(age = age, weight = weightKg, onAgeChange = { age = it }, onWeightChange = { weightKg = it }) {
                    if (age.isNotBlank() && weightKg.isNotBlank()) step = 2
                }
                2 -> StepGoal(selected = goal, onSelect = { goal = it }) { step = 3 }
                3 -> StepEquipment(
                    fitnessLevel = fitnessLevel,
                    onLevelSelect = { fitnessLevel = it },
                    selectedEquipment = selectedEquipment,
                    onEquipmentToggle = { eq ->
                        selectedEquipment = if (eq in selectedEquipment)
                            selectedEquipment - eq else selectedEquipment + eq
                    }
                ) {
                    onComplete(
                        UserProfile(
                            name = name.trim(),
                            age = age.toIntOrNull() ?: 0,
                            weightKg = weightKg.toFloatOrNull() ?: 0f,
                            goal = goal,
                            fitnessLevel = fitnessLevel,
                            availableEquipment = selectedEquipment.toList(),
                            jackedScore = 0,
                            totalSessions = 0,
                            currentStreak = 0
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun StepName(name: String, onNameChange: (String) -> Unit, onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("💪", fontSize = 56.sp)
        Spacer(Modifier.height(16.dp))
        Text("WHAT'S YOUR NAME?", style = MaterialTheme.typography.headlineLarge, color = ForgeOnSurface, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("We'll use this to personalize your experience", style = MaterialTheme.typography.bodyMedium, color = ForgeOnSurfaceDim, textAlign = TextAlign.Center)
        Spacer(Modifier.height(40.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter your name", color = ForgeOnSurfaceDim) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForgeOrange,
                unfocusedBorderColor = ForgeSurfaceVariant,
                focusedTextColor = ForgeOnSurface,
                unfocusedTextColor = ForgeOnSurface,
                cursorColor = ForgeOrange
            ),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(32.dp))
        ForgeButton("LET'S GO 🔥", enabled = name.isNotBlank(), onClick = onNext)
    }
}

@Composable
private fun StepBody(age: String, weight: String, onAgeChange: (String) -> Unit, onWeightChange: (String) -> Unit, onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("📊", fontSize = 56.sp)
        Spacer(Modifier.height(16.dp))
        Text("YOUR STATS", style = MaterialTheme.typography.headlineLarge, color = ForgeOnSurface, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("Helps the AI build the right workout for your body", style = MaterialTheme.typography.bodyMedium, color = ForgeOnSurfaceDim, textAlign = TextAlign.Center)
        Spacer(Modifier.height(40.dp))
        OutlinedTextField(
            value = age,
            onValueChange = onAgeChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Age", color = ForgeOnSurfaceDim) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForgeOrange, unfocusedBorderColor = ForgeSurfaceVariant, focusedTextColor = ForgeOnSurface, unfocusedTextColor = ForgeOnSurface, cursorColor = ForgeOrange),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = weight,
            onValueChange = onWeightChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Weight (kg)", color = ForgeOnSurfaceDim) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForgeOrange, unfocusedBorderColor = ForgeSurfaceVariant, focusedTextColor = ForgeOnSurface, unfocusedTextColor = ForgeOnSurface, cursorColor = ForgeOrange),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(32.dp))
        ForgeButton("NEXT", enabled = age.isNotBlank() && weight.isNotBlank(), onClick = onNext)
    }
}

@Composable
private fun StepGoal(selected: FitnessGoal, onSelect: (FitnessGoal) -> Unit, onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🎯", fontSize = 56.sp)
        Spacer(Modifier.height(16.dp))
        Text("YOUR GOAL", style = MaterialTheme.typography.headlineLarge, color = ForgeOnSurface, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("What are you training for?", style = MaterialTheme.typography.bodyMedium, color = ForgeOnSurfaceDim, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        FitnessGoal.entries.forEach { goal ->
            val isSelected = goal == selected
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) ForgeOrange else ForgeSurface)
                    .border(1.dp, if (isSelected) ForgeOrange else ForgeSurfaceVariant, RoundedCornerShape(12.dp))
                    .clickable { onSelect(goal) }
                    .padding(16.dp)
            ) {
                Text(goal.displayName, style = MaterialTheme.typography.titleMedium, color = if (isSelected) ForgeBackground else ForgeOnSurface, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(32.dp))
        ForgeButton("NEXT", onClick = onNext)
    }
}

@Composable
private fun StepEquipment(
    fitnessLevel: FitnessLevel,
    onLevelSelect: (FitnessLevel) -> Unit,
    selectedEquipment: Set<Equipment>,
    onEquipmentToggle: (Equipment) -> Unit,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🏋️", fontSize = 56.sp)
        Spacer(Modifier.height(16.dp))
        Text("YOUR SETUP", style = MaterialTheme.typography.headlineLarge, color = ForgeOnSurface, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("Fitness level + what equipment you have", style = MaterialTheme.typography.bodyMedium, color = ForgeOnSurfaceDim, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Text("FITNESS LEVEL", style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FitnessLevel.entries.forEach { level ->
                val isSelected = level == fitnessLevel
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) ForgeOrange else ForgeSurface)
                        .border(1.dp, if (isSelected) ForgeOrange else ForgeSurfaceVariant, RoundedCornerShape(10.dp))
                        .clickable { onLevelSelect(level) }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(level.displayName, style = MaterialTheme.typography.labelLarge, color = if (isSelected) ForgeBackground else ForgeOnSurface, textAlign = TextAlign.Center)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("EQUIPMENT", style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(8.dp))
        Equipment.entries.forEach { eq ->
            val isSelected = eq in selectedEquipment
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) ForgeOrange.copy(alpha = 0.15f) else ForgeSurface)
                    .border(1.dp, if (isSelected) ForgeOrange else ForgeSurfaceVariant, RoundedCornerShape(10.dp))
                    .clickable { onEquipmentToggle(eq) }
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isSelected, onCheckedChange = { onEquipmentToggle(eq) }, colors = CheckboxDefaults.colors(checkedColor = ForgeOrange, uncheckedColor = ForgeOnSurfaceDim))
                    Spacer(Modifier.width(8.dp))
                    Text(eq.displayName, style = MaterialTheme.typography.bodyLarge, color = ForgeOnSurface)
                }
            }
        }
        Spacer(Modifier.height(32.dp))
        ForgeButton("LET'S GET JACKED 💪", onClick = onComplete)
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ForgeButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ForgeOrange, disabledContainerColor = ForgeSurfaceVariant),
        enabled = enabled
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = if (enabled) ForgeBackground else ForgeOnSurfaceDim)
    }
}
