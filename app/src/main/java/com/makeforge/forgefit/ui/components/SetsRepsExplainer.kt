package com.makeforge.forgefit.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makeforge.forgefit.ui.theme.*

/**
 * Plain-English breakdown of notation like "4 x 8-12".
 * Collapsed by default so it never gets in the way once you know it.
 */
@Composable
fun SetsRepsExplainer(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ForgeSurface)
            .border(1.dp, ForgeSurfaceVariant, RoundedCornerShape(14.dp))
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "What does \"4 x 8-12\" mean?",
                style = MaterialTheme.typography.titleMedium,
                color = ForgeOrange,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (expanded) "HIDE" else "TAP",
                style = MaterialTheme.typography.labelLarge,
                color = ForgeOnSurfaceDim,
                fontSize = 10.sp
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(Modifier.height(14.dp))

                ExplainRow(
                    term = "REP",
                    short = "repetition",
                    detail = "One complete movement. Lower into a squat and stand back up once, that is 1 rep."
                )
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = ForgeSurfaceVariant, thickness = 0.5.dp)
                Spacer(Modifier.height(12.dp))

                ExplainRow(
                    term = "SET",
                    short = "a round of reps",
                    detail = "A group of reps done back to back. After each set you rest, then start the next one."
                )
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = ForgeSurfaceVariant, thickness = 0.5.dp)
                Spacer(Modifier.height(12.dp))

                ExplainRow(
                    term = "8-12",
                    short = "a rep range",
                    detail = "Aim for at least 8. If you can pass 12 with good form, the exercise is too easy so add weight or slow down."
                )

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ForgeOrange.copy(alpha = 0.12f))
                        .border(1.dp, ForgeOrange.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "So \"4 x 8-12\" means:",
                            style = MaterialTheme.typography.labelLarge,
                            color = ForgeOrange
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Do the movement 8 to 12 times. Rest. That is 1 set done. Repeat until you have finished 4 sets.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ForgeOnSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExplainRow(term: String, short: String, detail: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .width(64.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(ForgeSurfaceVariant)
                .padding(vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(term, style = MaterialTheme.typography.labelLarge, color = ForgeOrange, fontSize = 11.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(short, style = MaterialTheme.typography.titleMedium, color = ForgeOnSurface, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(3.dp))
            Text(detail, style = MaterialTheme.typography.bodyMedium, color = ForgeOnSurfaceDim)
        }
    }
}
