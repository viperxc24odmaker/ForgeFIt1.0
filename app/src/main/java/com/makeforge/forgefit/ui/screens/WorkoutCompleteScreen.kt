package com.makeforge.forgefit.ui.screens

import androidx.compose.foundation.background
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
import com.makeforge.forgefit.ui.theme.*

@Composable
fun WorkoutCompleteScreen(exerciseCount: Int, onHome: () -> Unit) {
    val jackedPoints = exerciseCount * 10

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔥", fontSize = 80.sp)
        Spacer(Modifier.height(24.dp))
        Text(
            "WORKOUT\nCOMPLETE!",
            style = MaterialTheme.typography.displayMedium,
            color = ForgeOrange,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "You absolutely crushed it. Your dad would be proud.",
            style = MaterialTheme.typography.bodyLarge,
            color = ForgeOnSurfaceDim,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(40.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ForgeSurface)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("💪", fontSize = 28.sp)
                Spacer(Modifier.height(8.dp))
                Text("$exerciseCount", style = MaterialTheme.typography.headlineLarge, color = ForgeOnSurface, fontWeight = FontWeight.Black)
                Text("EXERCISES", style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim, fontSize = 10.sp)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ForgeSurface)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🔥", fontSize = 28.sp)
                Spacer(Modifier.height(8.dp))
                Text("+$jackedPoints", style = MaterialTheme.typography.headlineLarge, color = ForgeOrange, fontWeight = FontWeight.Black)
                Text("JACKED PTS", style = MaterialTheme.typography.labelLarge, color = ForgeOnSurfaceDim, fontSize = 10.sp)
            }
        }

        Spacer(Modifier.height(48.dp))

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
