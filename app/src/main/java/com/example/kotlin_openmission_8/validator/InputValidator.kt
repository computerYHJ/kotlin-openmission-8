package com.example.kotlin_openmission_8.validator

import android.util.Log
import com.example.kotlin_openmission_8.User
import java.text.ParseException
import java.text.SimpleDateFormat

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
            else -> 99
        }
    }

    fun validatorPwd(pwd: String, samePwd: String): Int{
        return when(pwd.isEmpty()){
            true -> 1
            false -> when{
                samePwd.isEmpty() -> 2
                pwd != samePwd -> 3
                else -> 4
            }
        }
    }

    fun validatorDay(user: User, input: String): Boolean{
        val formatter = SimpleDateFormat("yyyy-MM-dd")

        var flag = true
        if(!user.startWorkout.isEmpty()){
            try{
                val start = formatter.parse(user.startWorkout)
                val end = formatter.parse(input)
                flag = start!! > end
            } catch (e: ParseException) { Log.d("dayError", e.toString())}
        }
        return flag
    }
}