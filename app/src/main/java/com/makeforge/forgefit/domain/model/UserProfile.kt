package com.makeforge.forgefit.domain.model

data class UserProfile(
    val name: String = "",
    val age: Int = 0,
    val weightKg: Float = 0f,
    val heightCm: Float = 0f,
    val goal: FitnessGoal = FitnessGoal.BUILD_MUSCLE,
    val fitnessLevel: FitnessLevel = FitnessLevel.BEGINNER,
    val availableEquipment: List<Equipment> = emptyList(),
    val jackedScore: Int = 0,
    val totalSessions: Int = 0,
    val currentStreak: Int = 0
)

enum class FitnessGoal(val displayName: String) {
    BUILD_MUSCLE("Build Muscle"),
    LOSE_FAT("Lose Fat"),
    GET_SHREDDED("Get Shredded"),
    STAY_FIT("Stay Fit")
}

enum class FitnessLevel(val displayName: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced")
}

enum class Equipment(val displayName: String) {
    NONE("No Equipment"),
    DUMBBELLS("Dumbbells"),
    BARBELL("Barbell"),
    PULL_UP_BAR("Pull-up Bar"),
    RESISTANCE_BANDS("Resistance Bands"),
    FULL_GYM("Full Gym")
}
