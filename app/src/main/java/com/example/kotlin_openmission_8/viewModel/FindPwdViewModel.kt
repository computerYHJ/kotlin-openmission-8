package com.example.kotlin_openmission_8.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_openmission_8.model.FindResult
import com.example.kotlin_openmission_8.model.LoginState
import com.example.kotlin_openmission_8.model.UserRepository
import com.example.kotlin_openmission_8.validator.EmptyResult
import com.example.kotlin_openmission_8.validator.InputValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FindPwdViewModel: ViewModel(){

    private val _uiState = MutableStateFlow(LoginState())
    val uiState = _uiState.asStateFlow()

    fun findPwd(id: String, email: String){
        viewModelScope.launch {
            if(!noInput(id,email)) return@launch
            attemptFindPwd(id, email)
        }
    }

    private fun noInput(id: String, email: String): Boolean {
        val result = InputValidator.validatorEmptyField(listOf(id,email))
        return when(result){
            EmptyResult.EMPTY_FIELD -> {_uiState.value = LoginState(errorMessage = "아이디와 이메일을 입력하세요"); false }
            EmptyResult.FIRST_EMPTY -> {_uiState.value = LoginState(errorMessage = "아이디를 입력하세요"); false }
            EmptyResult.SECOND_EMPTY -> {_uiState.value = LoginState(errorMessage = "이메일을 입력하세요"); false }
            else -> true
        }
    }

    private suspend fun attemptFindPwd(id: String, email: String) {
        when(val result = UserRepository.attemptFindPwd(id, email)){
            is FindResult.Ok -> _uiState.value = LoginState(successUserId = result.userId)
            FindResult.IdError -> _uiState.value = LoginState(errorMessage = "아이디나 이메일이 일치하지 않습니다")
            FindResult.UnknownError -> _uiState.value = LoginState(errorMessage = "오류가 발생했습니다. 관리자에게 문의해주세요")
            else -> null
        }
    }
}