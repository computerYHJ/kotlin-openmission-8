package com.example.kotlin_openmission_8.model

data class RegisterState(
    val id: String = "",
    val idError: String = "",
    val idFlag: Boolean = false,
    val dupliIdFlag: Boolean = false,

    val pwd: String = "",
    val pwdError: String = "",
    val samePwdError: String = "",
    val pwdFlag: Boolean = false,

    val email: String = "",
    val emailError: String = "",
    val emailFlag: Boolean = false,
    val dupliEmailFlag: Boolean = false,

    val name: String = "",
    val nameError: String = "",

    val registerError: String = ""
)
