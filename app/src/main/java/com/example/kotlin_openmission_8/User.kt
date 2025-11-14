package com.example.kotlin_openmission_8

data class User(
    var userID: String = "",
    var userPW: String = "",
    var userName: String = "",
    var userEmail: String = "",
    var startWorkout: String = "2025-11-12",
    var endWorkout: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var monthGoal: Int = 0,
    var workoutCount: Int = 0
)

