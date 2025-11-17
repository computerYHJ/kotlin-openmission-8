package com.example.kotlin_openmission_8.activity

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin_openmission_8.databinding.RegisterBinding
import com.example.kotlin_openmission_8.viewModel.RegisterViewModel
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.kotlin_openmission_8.model.RegisterEvent
import com.example.kotlin_openmission_8.model.RegisterState
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: RegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
        observeUiState()
    }

    private fun initListener() = with(binding) {
        registerIdDuplicateBtn.isEnabled = false
        registerEmailDuplicateBtn.isEnabled = false

        changeTextListener()
        clickBtnListener()

        registerSamePwdText.onFocusChangeListener = View.OnFocusChangeListener { view, focus ->
            registerSamePwdText.filters = arrayOf(InputFilter.LengthFilter(20 + 1))
            val pwd: String = registerPwdText.text.toString()
            val samePwd: String = registerSamePwdText.text.toString()
            if (focus) viewModel.checkSamePwd(pwd, samePwd)
            else viewModel.checkSamePwd(pwd, samePwd)
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        render(state)
                    }
                }
                launch {
                    viewModel.eventFlow.collect { event ->
                        when (event) {
                            RegisterEvent.Success -> finish()
                            RegisterEvent.Failed -> Toast.makeText(
                                this@RegisterActivity,
                                "잘못된 입력이 존재합니다.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun render(state: RegisterState) = with(binding) {

        registerIdMessageText.apply {
            text = state.idError
            visibility = View.VISIBLE
            setTextColor(Color.RED)
            if (state.dupliIdFlag) {
                setTextColor(Color.BLUE)
            } else {
                setTextColor(Color.RED)
            }
        }

        if (!state.idFlag) registerIdDuplicateBtn.isEnabled = false
        else registerIdDuplicateBtn.isEnabled = true

        registerPwdMessage.apply {
            text = state.pwdError
            visibility = View.VISIBLE
            setTextColor(Color.RED)
        }

        registerEmailMessage.apply {
            text = state.emailError
            visibility = View.VISIBLE
            setTextColor(Color.RED)
            if (state.dupliEmailFlag) {
                setTextColor(Color.BLUE)
            } else {
                setTextColor(Color.RED)
            }
        }

        if (!state.emailFlag) registerEmailDuplicateBtn.isEnabled = false
        else registerEmailDuplicateBtn.isEnabled = true

        registerSamePwdMsg.apply {
            text = state.samePwdError
            visibility = View.VISIBLE
            if (state.pwd.isEmpty()) setTextColor(Color.RED) else setTextColor(Color.BLUE)
        }

        registerNameMessage.apply {
            text = state.nameError
            visibility = View.VISIBLE
            setTextColor(Color.RED)
        }
    }

    private fun changeTextListener() = with(binding){
        registerIdText.addTextChangedListener { s ->
            viewModel.checkId(s.toString(), 6, 15)
        }

        registerPwdText.addTextChangedListener { s ->
            viewModel.checkPwd(s.toString(), 8, 20)
        }

        registerEmailText.addTextChangedListener { s ->
            viewModel.checkEmail(s.toString(), 6, 15)
        }

        registerNameEdit.addTextChangedListener { s ->
            registerNameEdit.filters = arrayOf(InputFilter.LengthFilter(11))
            viewModel.checkName(s.toString())
        }
    }

    private fun clickBtnListener() = with(binding){
        registerIdDuplicateBtn.setOnClickListener {
            val id: String = registerIdText.text.toString()
            viewModel.duplicate(id, "userID")
        }

        registerEmailDuplicateBtn.setOnClickListener {
            val email: String = registerEmailText.text.toString()
            viewModel.duplicate(email, "userEmail")
        }

        registerBtn.setOnClickListener { viewModel.register() }
    }

}