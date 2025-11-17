package com.example.kotlin_openmission_8.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.kotlin_openmission_8.model.UserRepository
import com.example.kotlin_openmission_8.databinding.FindPwdBinding
import com.example.kotlin_openmission_8.model.LoginState
import com.example.kotlin_openmission_8.validator.EmptyResult
import com.example.kotlin_openmission_8.validator.InputValidator
import com.example.kotlin_openmission_8.viewModel.FindPwdViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class FindPwdActivity : AppCompatActivity() {

    private lateinit var binding: FindPwdBinding
    private val viewModel: FindPwdViewModel by viewModels()
    private var db = FirebaseFirestore.getInstance()
    private var dbUsers = db.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FindPwdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
        observeUiState()
    }

    private fun initListener() = with(binding) {
        previousButton.setOnClickListener { finish() }
        findPwBtn.setOnClickListener {
            val id: String = findPwdIdText.text.toString()
            val email: String = findPwdEmailText.text.toString()
            viewModel.findPwd(id, email)
        }
    }

    private fun observeUiState(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: LoginState) {
        when {
            !state.errorMessage.isEmpty() -> showError(state.errorMessage)
            !state.successUserId.isEmpty() -> printResult()
        }
    }

//    private fun clickLoginBtn() = with(binding) {
//        val id: String = findPwdIdText.text.toString()
//        val email: String = findPwdEmailText.text.toString()
//        if(noInput(id,email)) {
//            lifecycleScope.launch { attemptFindPwd(id, email) }
//        }
//    }
//
    private fun showError(message: String){
        binding.findPwdFailText.text = message
        binding.findPwdFailText.visibility = View.VISIBLE
    }

    private fun printResult(){
        binding.findPwResultLayout.visibility = View.VISIBLE
        binding.findPwResultTextView.text = "비밀번호 재설정 이메일이 전송되었습니다."
    }
//
//    private fun noInput(id: String, email: String): Boolean = with(binding) {
//        val result = InputValidator.validatorEmptyField(listOf(id,email))
//        return when(result){
//            EmptyResult.EMPTY_FIELD -> {showError("아이디와 이메일을 입력해주세요"); false}
//            EmptyResult.FIRST_EMPTY -> {showError("아이디를 입력해주세요"); false}
//            EmptyResult.SECOND_EMPTY -> {showError("이메일을 입력해주세요"); false}
//            else -> true
//        }
//    }
//
//    private suspend fun attemptFindPwd(id: String, email: String) {
//        when(UserRepository.attemptFindPwd(id, email)){
//            1 -> showError("아이디나 이메일이 일치하지 않습니다")
//            2 -> printResult()
//            0 -> showError("오류가 발생했습니다")
//        }
//    }
}