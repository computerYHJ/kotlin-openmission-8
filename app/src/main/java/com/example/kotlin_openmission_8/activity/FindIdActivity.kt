package com.example.kotlin_openmission_8.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.kotlin_openmission_8.model.UserRepository
import com.example.kotlin_openmission_8.databinding.FindIdBinding
import com.example.kotlin_openmission_8.model.LoginState
import com.example.kotlin_openmission_8.validator.EmptyResult
import com.example.kotlin_openmission_8.validator.InputValidator
import com.example.kotlin_openmission_8.viewModel.FindIdViewModel
import kotlinx.coroutines.launch

class FindIdActivity: AppCompatActivity() {

    private lateinit var binding: FindIdBinding
    private val viewModel: FindIdViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FindIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
        observeUiState()
    }

    private fun initListener() = with(binding){
        previousButton.setOnClickListener { finish() }
        findIdBtn.setOnClickListener {
            val name: String = findIdNameText.text.toString()
            val email: String = findIdEmailText.text.toString()
            viewModel.findId(name, email)
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: LoginState) {
        when {
            !state.errorMessage.isEmpty() -> showError(state.errorMessage)
            !state.successUserId.isEmpty() -> find(state.successUserId)
        }
    }

    private fun showError(message: String) {
        binding.findIdFailText.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

        private fun find(id: String) {
        binding.findIdResultLayout.visibility = View.VISIBLE
        binding.findIdResultTextView.text = id
        binding.findIdFailText.visibility = View.INVISIBLE
        binding.findIdBtn.text = "로그인 화면 이동"
        binding.findIdBtn.setOnClickListener { finish() }
    }


//    private fun clickLoginBtn() = with(binding) {
//        val name: String = findIdNameText.text.toString()
//        val email: String = findIdEmailText.text.toString()
//        if(noInput(name,email)) lifecycleScope.launch { attemptFindId(name,email) }
//    }
//
//    private fun showError(message: String){
//        binding.findIdFailText.text = message
//        binding.findIdFailText.visibility = View.VISIBLE
//    }
//
//    private fun noInput(name: String, email: String): Boolean = with(binding) {
//        val result = InputValidator.validatorEmptyField(listOf(name,email))
//        return when(result){
//            EmptyResult.EMPTY_FIELD -> {showError("이름과 이메일을 입력해주세요"); false}
//            EmptyResult.FIRST_EMPTY -> {showError("이름을 입력해주세요"); false}
//            EmptyResult.SECOND_EMPTY -> {showError("이메일을 입력해주세요"); false}
//            else -> true
//        }
//    }
//
//    suspend private fun attemptFindId(name: String, email: String) {
//        val id = UserRepository.attemptFindId(name, email)
//        when(id){
//            "0" -> showError("오류가 발생했습니다. 관리자에게 문의해주세요")
//            "1" -> showError("이름이나 이메일이 잘못되었습니다")
//            "2" -> showError("아이디가 잘못되었습니다. 관리자에게 문의해주세요")
//            else -> find(id)
//        }
//    }
//
//    private fun find(id: String) {
//        binding.findIdResultLayout.visibility = View.VISIBLE
//        binding.findIdResultTextView.text = id
//        binding.findIdFailText.visibility = View.INVISIBLE
//        binding.findIdBtn.text = "로그인 화면 이동"
//        binding.findIdBtn.setOnClickListener { finish() }
//    }
}