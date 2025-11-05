package com.example.kotlin_openmission_8.validator

object InputValidator {

    fun validatorEmptyField(fields: List<String>): Int{
        return when{
            fields[0].isEmpty() && fields[1].isEmpty() -> 0
            fields[0].isEmpty() -> 1
            fields[1].isEmpty() -> 2
            else -> 3
        }
    }
}