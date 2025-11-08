package com.example.kotlin_openmission_8

data class User(
    var userID: String = "",
    val userPW: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val startWorkout: String = "",
    val endWorkout: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val monthGoal: String = "",
    val workoutCount: Int = 0
)

