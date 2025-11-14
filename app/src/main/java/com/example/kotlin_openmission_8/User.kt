package com.example.kotlin_openmission_8

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class User(
    var userID: String = "",
    var userPW: String = "",
    var userName: String = "",
    var userEmail: String = "",
    var startWorkout: String = today(),
    var endWorkout: String = today(),
    var startTime: String = "",
    var endTime: String = "",
    var monthGoal: Int = 0,
    var workoutCount: Int = 0
){
    companion object {
        fun today(): String =
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}


