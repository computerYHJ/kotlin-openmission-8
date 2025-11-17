package com.example.kotlin_openmission_8.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_openmission_8.model.DuplicateInput
import com.example.kotlin_openmission_8.model.RegisterEvent
import com.example.kotlin_openmission_8.model.RegisterState
import com.example.kotlin_openmission_8.model.User
import com.example.kotlin_openmission_8.model.UserRepository
import com.example.kotlin_openmission_8.validator.InfoInputResult
import com.example.kotlin_openmission_8.validator.InputValidator
import com.example.kotlin_openmission_8.validator.PasswordResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<RegisterEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun checkId(input: String, start: Int, end: Int) {
        val current = _uiState.value

        when (InputValidator.validatorInput(input, start, end, "아이디")) {
            InfoInputResult.WRONG_FORM_INPUT ->
                _uiState.value = current.copy(idError = "형식에 맞지 않습니다", idFlag = false)

            InfoInputResult.TOO_SHORT ->
                _uiState.value = current.copy(idError = "아이디는 ${start}자 이상 입력해주세요.", idFlag = false)

            InfoInputResult.TOO_LONG ->
                _uiState.value = current.copy(idError = "아이디는 ${end}자 이하로 입력해주세요.", idFlag = false)

            InfoInputResult.OK ->
                _uiState.value = current.copy(idError = "", idFlag = true)

            else -> ""
        }
    }

    fun checkPwd(input: String, start: Int, end: Int) {
        val current = _uiState.value

        when (InputValidator.validatorInput(input, start, end, "비밀번호")) {
            InfoInputResult.WRONG_FORM_INPUT ->
                _uiState.value = current.copy(pwdError = "형식에 맞지 않습니다", pwdFlag = false)

            InfoInputResult.TOO_SHORT ->
                _uiState.value =
                    current.copy(pwdError = "비밀번호는 ${start}자 이상 입력해주세요.", pwdFlag = false)

            InfoInputResult.TOO_LONG ->
                _uiState.value =
                    current.copy(pwdError = "비밀번호는 ${end}자 이하로 입력해주세요.", pwdFlag = false)

            InfoInputResult.OK ->
                _uiState.value = current.copy(pwdError = "", pwdFlag = true)

            else -> ""
        }
    }

    fun checkEmail(input: String, start: Int, end: Int) {
        val current = _uiState.value

        when (InputValidator.validatorInput(input, start, end, "email")) {
            InfoInputResult.EMAIL_PATTERN_WRONG -> _uiState.value =
                current.copy(emailError = "이메일 형식에 맞지 않습니다.", emailFlag = false)

            InfoInputResult.OK -> _uiState.value = current.copy(emailError = "", emailFlag = true)
            else -> ""
        }
    }

    fun duplicate(input: String, fields: String) {
        viewModelScope.launch {
            when (fields) {
                "userID" -> duplicateId(input, fields)
                "userEmail" -> duplicateEmail(input, fields)
            }
        }
    }

    private suspend fun duplicateId(input: String, fields: String) {
        val current = _uiState.value
        val state = UserRepository.checkDuplicate(input, fields)
        when (state) {
            DuplicateInput.OK -> {
                _uiState.value = current.copy(
                    idError = "사용할 수 있는 아이디입니다.", dupliIdFlag = true,
                    id = if (current.idFlag) input else ""
                )
            }

            DuplicateInput.ALREADY_INPUT_DATA -> {
                _uiState.value = current.copy(
                    idError = "중복된 아이디가 존재합니다.", dupliIdFlag = false,
                    id = if (current.idFlag) "" else ""
                )
            }

            DuplicateInput.UNKNOWN_ERROR -> ""
        }
    }

    private suspend fun duplicateEmail(input: String, fields: String) {
        val current = _uiState.value
        val state = UserRepository.checkDuplicate(input, fields)
        when (state) {
            DuplicateInput.OK -> {
                _uiState.value = current.copy(
                    emailError = "사용할 수 있는 이메일입니다.", dupliEmailFlag = true,
                    email = if (current.emailFlag) input else ""
                )
            }

            DuplicateInput.ALREADY_INPUT_DATA -> {
                _uiState.value = current.copy(
                    emailError = "중복된 이메일이 존재합니다.", dupliEmailFlag = false,
                    email = if (current.emailFlag) "" else ""
                )
            }

            DuplicateInput.UNKNOWN_ERROR -> ""
        }
    }

    fun checkSamePwd(pwd: String, samePwd: String) {
        val current = _uiState.value
        when (InputValidator.validatorPwd(pwd, samePwd)) {
            PasswordResult.EMPTY_PASSWORD -> _uiState.value =
                current.copy(samePwdError = "비밀번호를 먼저 입력하세요.", pwd = "")

            PasswordResult.EMPTY_SAME_PASSWORD -> _uiState.value =
                current.copy(samePwdError = "비밀번호를 다시 입력하세요.", pwd = "")

            PasswordResult.WRONG_PASSWORD -> _uiState.value =
                current.copy(samePwdError = "비밀번호가 일치하지 않습니다.", pwd = "")

            PasswordResult.OK -> if (current.pwdFlag) _uiState.value =
                current.copy(samePwdError = "사용가능한 비밀번호입니다.", pwd = samePwd)
        }
    }

    fun checkName(input: String) {
        val current = _uiState.value
        when (input.trim().length) {
            0 -> _uiState.value = current.copy(nameError = "이름은 공백이 될 수 없습니다.")
            11 -> _uiState.value = current.copy(nameError = "최대 허용 글자를 넘어섰습니다.")
            else -> _uiState.value = current.copy(nameError = "", name = input)
        }
        Log.d("password", _uiState.value.name)
    }

    fun register() {
        viewModelScope.launch { tryRegister() }
    }

    private suspend fun tryRegister() {
        val current = _uiState.value
        Log.d("Register", "${current.id}, ${current.pwd}, ${current.name}")
        if (!current.id.isEmpty() && !current.pwd.isEmpty() && !current.email.isEmpty()) {
            _uiState.value = current.copy(registerError = "성공")
            val user: User = User()
            user.apply { userID = current.id; userPW = current.pwd; userEmail = current.email }
            UserRepository.register(user)
            _eventFlow.emit(RegisterEvent.Success)
        } else _eventFlow.emit(RegisterEvent.Failed)
    }
}

