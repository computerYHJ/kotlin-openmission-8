package com.example.kotlin_openmission_8.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.kotlin_openmission_8.databinding.LoginBinding
import com.example.kotlin_openmission_8.model.LoginState
import com.example.kotlin_openmission_8.viewModel.LoginViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
        observeUiState()
    }
    private fun initListeners() = with(binding) {
        previousButton.setOnClickListener { finish() }

        finalLoginBtn.setOnClickListener {
            val id = loginInputIdEditText.text.toString()
            val pwd = loginInputPasswordEditText.text.toString()
            viewModel.login(id, pwd)
        }

        findIdLoginPage.setOnClickListener {
            startActivity(Intent(this@LoginActivity, FindIdActivity::class.java))
        }

        findPwLoginPage.setOnClickListener {
            startActivity(Intent(this@LoginActivity, FindPwdActivity::class.java))
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
            !state.successUserId.isEmpty() -> navigateToUserView(state.successUserId)
        }
    }

    private fun showError(message: String) {
        binding.loginFailText.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun navigateToUserView(userId: String) {
        val intent = Intent(this, UserViewActivity::class.java)
        intent.putExtra("userID", userId)
        startActivity(intent)
    }
}
