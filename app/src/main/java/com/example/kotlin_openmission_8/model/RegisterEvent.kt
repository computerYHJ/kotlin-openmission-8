package com.example.kotlin_openmission_8.model

sealed class RegisterEvent {
    object Success: RegisterEvent()
    object Failed: RegisterEvent()
}