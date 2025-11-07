package com.example.kotlin_openmission_8.validator

import com.google.firebase.firestore.QuerySnapshot

object InputValidator {

    fun validatorEmptyField(fields: List<String>): Int{
        return when{
            fields[0].isEmpty() && fields[1].isEmpty() -> 0
            fields[0].isEmpty() -> 1
            fields[1].isEmpty() -> 2
            else -> 3
        }
    }

    fun validatorInput(input: String, start: Int, end: Int, loc: String): Int{
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        var regex: Regex = Regex("""[^a-zA-Z0-9]""")
        return when{
            loc != "email" && regex.containsMatchIn(input) -> 1
            loc != "email" && input.length < start -> 2
            loc != "email" && input.length > end -> 3
            loc == "email" && !emailPattern.matcher(input).matches() -> 4
            input.isEmpty() -> 2
            else -> 99
        }
    }
}