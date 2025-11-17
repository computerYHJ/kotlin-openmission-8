package com.example.kotlin_openmission_8.validator

import android.util.Log
import com.example.kotlin_openmission_8.model.User
import java.text.ParseException
import java.text.SimpleDateFormat

object InputValidator {

    fun validatorEmptyField(fields: List<String>): EmptyResult{
        return when{
            fields[0].isEmpty() && fields[1].isEmpty() -> EmptyResult.EMPTY_FIELD
            fields[0].isEmpty() -> EmptyResult.FIRST_EMPTY
            fields[1].isEmpty() -> EmptyResult.SECOND_EMPTY
            else -> EmptyResult.OK
        }
    }
    fun validatorInput(input: String, start: Int, end: Int, loc: String): InfoInputResult{
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        var regex: Regex = Regex("""[^a-zA-Z0-9]""")
        return when{
            loc != "email" && regex.containsMatchIn(input) -> InfoInputResult.WRONG_FORM_INPUT
            loc != "email" && input.length < start -> InfoInputResult.TOO_SHORT
            loc != "email" && input.length > end -> InfoInputResult.TOO_LONG
            loc == "email" && !emailPattern.matcher(input).matches() -> InfoInputResult.EMAIL_PATTERN_WRONG
            else -> InfoInputResult.OK
        }
    }

    fun validatorPwd(pwd: String, samePwd: String): PasswordResult{
        return when(pwd.isEmpty()){
            true -> PasswordResult.EMPTY_PASSWORD
            false -> when{
                samePwd.isEmpty() -> PasswordResult.EMPTY_SAME_PASSWORD
                pwd != samePwd -> PasswordResult.WRONG_PASSWORD
                else -> PasswordResult.OK
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
                flag = start!! >= end
            } catch (e: ParseException) { Log.d("dayError", e.toString())}
        }
        return flag
    }
}