package com.example.kotlin_openmission_8.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_openmission_8.model.LoginResult
import com.example.kotlin_openmission_8.model.LoginState
import com.example.kotlin_openmission_8.model.UserRepository
import com.example.kotlin_openmission_8.validator.EmptyResult
import com.example.kotlin_openmission_8.validator.InputValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel(){
    private val _uiState = MutableStateFlow(LoginState())
    val uiState = _uiState.asStateFlow()

    fun login(id: String, pwd: String){
        viewModelScope.launch {
            if(!noInput(id, pwd)) return@launch
            attemptLogin(id, pwd)
        }
    }

    private fun noInput(id: String, pwd: String): Boolean{
        val result = InputValidator.validatorEmptyField(listOf(id,pwd))
        return when(result){
            EmptyResult.EMPTY_FIELD -> {_uiState.value =
                LoginState(errorMessage = "아이디나 비밀번호를 입력하세요"); false }
            EmptyResult.FIRST_EMPTY -> {_uiState.value = LoginState(errorMessage = "아이디를 입력하세요"); false }
            EmptyResult.SECOND_EMPTY -> {_uiState.value = LoginState(errorMessage = "비밀번호를 입력하세요"); false }
            EmptyResult.OK -> {_uiState.value = LoginState(errorMessage = ""); true }
        }
    }

    private suspend fun attemptLogin(id: String, pwd: String) {
        when(UserRepository.attemptLogin(id, pwd)){
            LoginResult.ID_WRONG ->  _uiState.value = LoginState(errorMessage = "아이디가 일치하지 않습니다")
            LoginResult.EMAIL_WRONG -> _uiState.value = LoginState(errorMessage = "이메일이 잘못되었습니다. 관리자에게 문의해주세요")
            LoginResult.PASSWORD_WRONG ->  _uiState.value = LoginState(errorMessage = "비밀번호가 일치하지 않습니다")
            LoginResult.OK -> _uiState.value = LoginState(successUserId = id)
        }
    }
}