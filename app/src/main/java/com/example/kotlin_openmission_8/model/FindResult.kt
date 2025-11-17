package com.example.kotlin_openmission_8.model

sealed class FindResult {
    data class Ok(val userId: String) : FindResult()
    object WrongInput: FindResult()
    object IdError: FindResult()

    object EmailError: FindResult()
    object UnknownError: FindResult()
}