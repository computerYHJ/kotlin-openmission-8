package com.example.kotlin_openmission_8.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_openmission_8.model.FindResult
import com.example.kotlin_openmission_8.model.UserRepository
import com.example.kotlin_openmission_8.model.LoginState
import com.example.kotlin_openmission_8.validator.EmptyResult
import com.example.kotlin_openmission_8.validator.InputValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FindIdViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState = _uiState.asStateFlow()

    fun findId(name: String, email: String){
        viewModelScope.launch {
            if(!noInput(name, email)) return@launch
            attemptFindId(name, email)
        }
    }


    private fun noInput(name: String, email: String): Boolean{
        val result = InputValidator.validatorEmptyField(listOf(name,email))
        return when(result){
            EmptyResult.EMPTY_FIELD -> {_uiState.value = LoginState(errorMessage = "이름과 이메일을 입력하세요"); false }
            EmptyResult.FIRST_EMPTY -> {_uiState.value = LoginState(errorMessage = "이름을 입력해주세요"); false }
            EmptyResult.SECOND_EMPTY -> {_uiState.value = LoginState(errorMessage = "이메일을 입력해주세요"); false }
            EmptyResult.OK -> {_uiState.value = LoginState(errorMessage = ""); true }
        }
    }

    private suspend fun attemptFindId(name: String, email: String) {
        when(val result = UserRepository.attemptFindId(name, email)){
            is FindResult.Ok -> {_uiState.value = LoginState(successUserId = result.userId) }
            FindResult.UnknownError -> { _uiState.value = LoginState(errorMessage = "오류가 발생했습니다. 관리자에게 문의해주세요") }
            FindResult.WrongInput -> {_uiState.value = LoginState(errorMessage = "이름이나 이메일을 잘못 입력했습니다") }
            FindResult.IdError -> {_uiState.value = LoginState(errorMessage = "아이디가 잘못되었습니다. 관리자에게 문의해주세요") }
            else -> null
        }
    }
}