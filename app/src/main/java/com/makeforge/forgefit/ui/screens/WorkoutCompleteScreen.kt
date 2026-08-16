package com.makeforge.forgefit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makeforge.forgefit.ui.theme.*

@Composable
fun WorkoutCompleteScreen(
    exerciseCount: Int,
    totalSets: Int,
    jackedPoints: Int,
    elapsedMinutes: Int,
    onHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔥", fontSize = 72.sp)
        Spacer(Modifier.height(20.dp))
        Text(
            "WORKOUT\nCOMPLETE",
            style = MaterialTheme.typography.displayMedium,
            color = ForgeOrange,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Every set logged. That is how it is done.",
            style = MaterialTheme.typography.bodyLarge,
            color = ForgeOnSurfaceDim,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(36.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryTile(Modifier.weight(1f), "💪", "$exerciseCount", "EXERCISES")
            SummaryTile(Modifier.weight(1f), "🎯", "$totalSets", "SETS")
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryTile(Modifier.weight(1f), "⏱️", "${elapsedMinutes}m", "DURATION")
            SummaryTile(Modifier.weight(1f), "🔥", "+$jackedPoints", "JACKED PTS", highlight = true)
        }

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = onHome,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ForgeOrange)
        ) {
            Text("BACK TO HOME", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = ForgeBackground)
        }
    }
}

@Composable
private fun SummaryTile(
    modifier: Modifier = Modifier,
    icon: String,
    value: String,
    label: String,
    highlight: Boolean = false
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ForgeSurface)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 24.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            value,
            style = MaterialTheme.typography.headlineLarge,
            color = if (highlight) ForgeOrange else ForgeOnSurface,
            fontWeight = FontWeight.Black
        )
        Text(label, style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim, fontSize = 9.sp)
    }
}
