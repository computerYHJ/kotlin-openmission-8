package com.example.kotlin_openmission_8

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.kotlin_openmission_8.databinding.RegisterBinding
import com.example.kotlin_openmission_8.validator.InputValidator
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: RegisterBinding

    private var id: String = ""
    private var password: String = ""
    private var email: String = ""

    private var flag: Int = -1
    private var pwdFlag: Int = -1
    private var emailFlag: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.previousButton.setOnClickListener { finish() }

        checkInput(binding.registerIdText, binding.registerIdMessageText, "아이디", 6, 15)
        checkInput(binding.registerPwdText, binding.registerPwdMessage, "비밀번호", 8, 20)
        checkInput(binding.registerEmailText, binding.registerEmailMessage, "email", 8, 20)

        binding.registerEmailDuplicateBtn.setOnClickListener {
            lifecycleScope.launch {
                duplicateBtn(
                    binding.registerEmailText,
                    binding.registerEmailMessage,
                    "userEmail",
                    "이메일"
                )
            }
        }

        binding.registerIdDuplicateBtn.setOnClickListener {
            lifecycleScope.launch { duplicateBtn(
                binding.registerIdText,
                binding.registerIdMessageText,
                "userID",
                "아이디"
            ) }
        }
        notEqualPwd()
        binding.registerBtn.setOnClickListener { lifecycleScope.launch { register() } }
    }

    private suspend fun duplicateBtn(editView: EditText, textView: TextView, fields: String, loc: String) {
        val input: String = editView.text.toString()
        when(fields){
            "userID" -> when(duplicate(fields, input, textView, loc)){
                1 -> if(flag == 99) id = input
                else -> id = ""
            }
            "userEmail" -> when(duplicate(fields, input, textView, loc)){
                1 -> if(emailFlag == 99) email = input
                else -> email = ""
            }
        }
        Log.d("CHECK INPUT ERROR", "$id, $email")
    }

    private suspend fun duplicate(
        fields: String,
        input: String,
        textView: TextView,
        loc: String
    ): Int{
        return when(UserRepository.checkDupliceate(input,fields)) {
            1 -> 1.also{resisterMessagePrint(textView, "사용할 수 있는 ${loc}입니다.", Color.BLUE)}
            2 -> 2.also{resisterMessagePrint(textView, "${loc}가(이) 입력되지 않았습니다.", Color.RED)}
            3 -> 3.also{resisterMessagePrint(textView, "중복된 ${loc}를(을) 입력했습니다.", Color.RED)}
            else -> 0
        }
    }

    private fun resisterMessagePrint(view: TextView, message: String, color: Int) {
        view.visibility = View.VISIBLE
        view.text = message
        view.setTextColor(color)
    }

    private fun checkInput(
        editView: EditText,
        textView: TextView,
        loc: String,
        start: Int,
        end: Int
    ) {
        editView.addTextChangedListener { s ->
            editView.filters = arrayOf(InputFilter.LengthFilter(end + 1))
            var target = s.toString()

            when (InputValidator.validatorInput(target, start, end, loc)) {
                1 -> {resisterMessagePrint(textView, "형식에 맞는 입력이 아닙니다.", Color.RED)
                    if(loc == "아이디") flag = 1; else pwdFlag = 1}
                2 -> { resisterMessagePrint(textView, "${loc}는 ${start}자 이상 입력해주세요.", Color.RED)
                    if(loc == "아이디") flag = 2; else pwdFlag = 2}
                3 -> { resisterMessagePrint(textView, "${loc}는 ${start}자 이하로 입력해주세요.", Color.RED)
                    if(loc == "아이디") flag = 3; else pwdFlag = 3}
                4 -> emailFlag = 4.also { resisterMessagePrint(textView, "이메일 형식에 맞지 않습니다.", Color.RED) }
                else -> { textView.visibility = View.INVISIBLE; emailFlag = 99
                    if(loc == "아이디") flag = 99; else pwdFlag = 99}
            }
        }
    }

    private fun notEqualPwd() = with(binding) {
        registerSamePwdText.onFocusChangeListener = View.OnFocusChangeListener { view, focus ->
            registerSamePwdText.filters = arrayOf(InputFilter.LengthFilter(20 + 1))
            val pwd: String = registerPwdText.text.toString()
            val samePwd: String = registerSamePwdText.text.toString()
            if (focus) checkSamePwd(pwd, samePwd)
            else checkSamePwd(pwd, samePwd)
        }
    }

    private fun checkSamePwd(pwd: String, samePwd: String) = with(binding) {
        when(InputValidator.validatorPwd(pwd, samePwd)){
            1 -> {resisterMessagePrint(registerSamePwdMsg, "비밀번호를 먼저 입력하세요.", Color.RED); password = ""}
            2 -> {resisterMessagePrint(registerSamePwdMsg, "비밀번호를 다시 입력하세요.", Color.RED); password = ""}
            3 -> {resisterMessagePrint(registerSamePwdMsg, "비밀번호가 일치하지 않습니다.", Color.RED); password = ""}
            4 -> {if(pwdFlag == 99) {registerSamePwdMsg.visibility = View.INVISIBLE; password = samePwd}
            else {resisterMessagePrint(registerSamePwdMsg, "비밀번호를 다시 입력하세요.", Color.RED); password = ""}}
        }
    }

    private suspend fun register() {
        Log.d("Register", "$id, $password, $email")
        if (!id.isEmpty() && !password.isEmpty() && !email.isEmpty()) {
            UserRepository.register(id,password,email)
        } else {
            Toast.makeText(this@RegisterActivity, "잘못된 입력이 존재합니다.", Toast.LENGTH_LONG).show()
        }
    }
}