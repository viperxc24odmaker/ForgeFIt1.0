package com.makeforge.forgefit.network

import com.google.gson.Gson
import com.makeforge.forgefit.domain.model.Exercise
import com.makeforge.forgefit.domain.model.UserProfile
import com.makeforge.forgefit.domain.model.Workout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutAiService @Inject constructor(
    private val openRouterApiService: OpenRouterApiService,
    private val gson: Gson
) {
    private val systemPrompt = """
        You are ForgeFit AI — a no-nonsense, elite personal trainer that designs workout plans to get people JACKED.
        You speak with intensity and confidence. You generate workouts optimized for real results.
        
        When asked to generate a workout, respond ONLY with a valid JSON object in this exact format:
        {
          "title": "WORKOUT TITLE IN CAPS",
          "durationMinutes": 45,
          "exercises": [
            {
              "name": "Exercise Name",
              "sets": 4,
              "reps": "8-12",
              "restSeconds": 90,
              "muscleGroup": "Chest",
              "instructions": "Short form cue here."
            }
          ]
        }
        
        No extra text, no markdown, no explanation. Pure JSON only.
    """.trimIndent()

    suspend fun generateWorkout(profile: UserProfile, availableMinutes: Int = 45): Result<Workout> {
        return try {
            val prompt = buildWorkoutPrompt(profile, availableMinutes)
            val response = openRouterApiService.chat(
                OpenRouterRequest(
                    messages = listOf(
                        OpenRouterMessage("system", systemPrompt),
                        OpenRouterMessage("user", prompt)
                    )
                )
            )
            val json = response.choices.firstOrNull()?.message?.content
                ?: return Result.failure(Exception("Empty response from AI"))

            val cleanJson = extractJson(json)
            val workoutData = gson.fromJson(cleanJson, WorkoutJsonData::class.java)
                ?: return Result.failure(Exception("AI returned malformed workout data"))
            val parsedExercises = workoutData.exercises
            if (parsedExercises.isNullOrEmpty()) {
                return Result.failure(Exception("AI returned no exercises. Try again."))
            }
            val workout = Workout(
                title = workoutData.title,
                exercises = parsedExercises.map { ex ->
                    Exercise(
                        name = ex.name,
                        sets = ex.sets,
                        reps = ex.reps,
                        restSeconds = ex.restSeconds,
                        muscleGroup = ex.muscleGroup,
                        instructions = ex.instructions
                    )
                },
                durationMinutes = workoutData.durationMinutes
            )
            Result.success(workout)
        } catch (e: Exception) {
            Result.failure(Exception(describeError(e)))
        }
    }

    suspend fun getMotivationalMessage(profile: UserProfile): Result<String> {
        return try {
            val response = openRouterApiService.chat(
                OpenRouterRequest(
                    messages = listOf(
                        OpenRouterMessage("system", "You are ForgeFit AI. Give a SHORT (1-2 sentences max), intense, motivational morning message. No emojis. Pure fire."),
                        OpenRouterMessage("user", "Give me my morning motivation. My goal is ${profile.goal.displayName}. I have a ${profile.currentStreak} day streak.")
                    )
                )
            )
            val text = response.choices.firstOrNull()?.message?.content ?: "GET UP AND GRIND."
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildWorkoutPrompt(profile: UserProfile, minutes: Int): String {
        val equipment = if (profile.availableEquipment.isEmpty()) "no equipment (bodyweight only)"
        else profile.availableEquipment.joinToString(", ") { it.displayName }

        return """
            Generate a ${minutes}-minute morning workout for:
            - Goal: ${profile.goal.displayName}
            - Fitness level: ${profile.fitnessLevel.displayName}
            - Available equipment: $equipment
            - Weight: ${profile.weightKg}kg
            
            Make it intense, effective, and designed to get them JACKED.
        """.trimIndent()
    }
}


/** Turns opaque HTTP failures into something readable on screen. */
private fun describeError(e: Exception): String {
    if (e is retrofit2.HttpException) {
        val body = runCatching { e.response()?.errorBody()?.string() }.getOrNull()
        val hint = when (e.code()) {
            401 -> "Invalid or missing API key."
            402 -> "OpenRouter account needs credits for this model."
            404 -> "Model not found - it may have been delisted."
            429 -> "Rate limited (free tier: 20/min, 200/day). Wait and retry."
            else -> "Request failed."
        }
        return "HTTP ${e.code()}: $hint" + if (!body.isNullOrBlank()) " ${body.take(180)}" else ""
    }
    if (e is java.io.IOException) return "Network error - check your connection."
    return e.message ?: "Unknown error"
}

/** Strips markdown fences / stray prose so Gson only ever sees the JSON object. */
private fun extractJson(raw: String): String {
    var s = raw.trim()
    if (s.startsWith("```")) {
        s = s.removePrefix("```json").removePrefix("```").trim()
        val end = s.lastIndexOf("```")
        if (end != -1) s = s.substring(0, end).trim()
    }
    val start = s.indexOf('{')
    val close = s.lastIndexOf('}')
    return if (start != -1 && close > start) s.substring(start, close + 1) else s
}

private data class WorkoutJsonData(
    val title: String,
    val durationMinutes: Int,
    val exercises: List<ExerciseJsonData>?
)

private data class ExerciseJsonData(
    val name: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val muscleGroup: String,
    val instructions: String
)
