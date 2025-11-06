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
import com.example.kotlin_openmission_8.databinding.RegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: RegisterBinding
    private var db = FirebaseFirestore.getInstance()
    private var dbUsers = db.collection("users")

    private var id: String = ""
    private var password: String = ""
    private var email: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.previousButton.setOnClickListener { finish() }

        checkRange(binding.registerIdText, binding.registerIdMessageText, "아이디", 6, 15)
        binding.registerIdDuplicateBtn.setOnClickListener {
            duplicateBtn(
                binding.registerIdText,
                binding.registerIdMessageText,
                "userID",
                "아이디"
            )
        }

        binding.registerEmailDuplicateBtn.setOnClickListener {
            duplicateBtn(
                binding.registerEmailText,
                binding.registerEmailMessage,
                "userEmail",
                "이메일"
            )
        }
        checkRange(binding.registerPwdText, binding.registerPwdMessage, "비밀번호", 8, 20)
        notEqualPwd()
        checkEmail(binding.registerEmailText, binding.registerEmailMessage)

        binding.registerBtn.setOnClickListener { register() }
    }

    private fun findWrongInput(input: String, textView: TextView): Boolean {
        return if (Regex("""[^a-zA-Z0-9]""").containsMatchIn(input)) {
            resisterMessagePrint(textView, "형식에 맞는 입력이 아닙니다.", Color.RED)
            true
        } else {
            false
        }
    }

    private fun duplicateBtn(editView: EditText, textView: TextView, fields: String, loc: String) {
        var input: String = editView.text.toString()
        if (fields == "userID") id = duplicate(input, fields, textView, loc)
        else if (fields == "userEmail") email = duplicate(input, fields, textView, loc)
    }

    private fun duplicate(input: String, fields: String, textView: TextView, loc: String): String {
        var correct: String = ""
        dbUsers.whereEqualTo(fields, input).get().addOnCompleteListener { result ->
            if (result.isSuccessful) {
                correct = MessageResult(result.result, input, textView, loc)
            }
        }
        return correct
    }

    private fun MessageResult(item: QuerySnapshot, input: String, textView: TextView, loc: String): String {
        var result: String = ""
        if (!item.isEmpty) {
            resisterMessagePrint(textView, "중복된 ${loc}를(을) 입력했습니다.", Color.RED)
            result = ""
        } else {
            if (textView.currentTextColor == Color.BLUE) {
                resisterMessagePrint(textView, "사용할 수 있는 ${loc}입니다.", Color.BLUE)
                result = input
            }
        }
        return result
    }
    private fun resisterMessagePrint(view: TextView, message: String, color: Int) {
        view.visibility = View.VISIBLE
        view.text = message
        view.setTextColor(color)
    }

    private fun checkRange(
        editView: EditText,
        textView: TextView,
        loc: String,
        start: Int,
        end: Int
    ) = with(binding) {
        editView.addTextChangedListener { s ->
            editView.filters = arrayOf(InputFilter.LengthFilter(end + 1))
            var target = s.toString()

            if (findWrongInput(target, textView)) return@addTextChangedListener

            var msg = when {
                target.length < start -> "${loc}는 ${start}자 이상이여야 합니다."
                target.length > end -> "${loc}는 ${end}자 이하이여야 합니다."
                else -> ""
            }
            if (!msg.isEmpty()) resisterMessagePrint(textView, msg, Color.RED)
            else resisterMessagePrint(textView, msg, Color.BLUE)
        }
    }

    private fun checkEmail(editView: EditText, textView: TextView){
        editView.addTextChangedListener { s ->
            var target = s.toString()
            val emailPattern = android.util.Patterns.EMAIL_ADDRESS
            var msg = when {
                !emailPattern.matcher(target).matches() -> "올바른 이메일 형식이 아닙니다"
                else -> ""
            }
            if (!msg.isEmpty()) resisterMessagePrint(textView, msg, Color.RED)
            else resisterMessagePrint(textView, msg, Color.BLUE)
        }
    }

    private fun notEqualPwd() = with(binding) {
        registerSamePwdText.onFocusChangeListener = View.OnFocusChangeListener { view, focus ->
            registerSamePwdText.filters = arrayOf(InputFilter.LengthFilter(20 + 1))
            val pwd: String = registerPwdText.text.toString()
            val samePwd: String = registerSamePwdText.text.toString()
            if (findWrongInput(samePwd, registerSamePwdMsg)) return@OnFocusChangeListener
            if (focus) checkSamePwd(pwd, samePwd)
            else checkSamePwd(pwd, samePwd)
        }
    }

    private fun checkSamePwd(pwd: String, samePwd: String) = with(binding) {
        if (pwd.isEmpty()) {
            resisterMessagePrint(registerSamePwdMsg, "비밀번호를 먼저 입력하세요.", Color.RED)
            password = ""
        } else {
            if (samePwd.isEmpty()) {
                resisterMessagePrint(registerSamePwdMsg, "비밀번호를 다시 입력하세요.", Color.RED)
                password = ""
            } else if (pwd != samePwd) {
                resisterMessagePrint(registerSamePwdMsg, "비밀번호가 일치하지 않습니다.", Color.RED)
                password = ""
            } else {
                resisterMessagePrint(registerSamePwdMsg, "", Color.RED)
                password = samePwd
            }
        }
    }

    private fun register() {
        var mAuth = FirebaseAuth.getInstance()
        Log.d("Register", id + ", " + password)
        if (!id.isEmpty() && !password.isEmpty() && !email.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        dbUsers.add(
                            User(
                                userID = id,
                                userEmail = email,
                                userPW = password
                            )
                        )
                    }
                }
        } else {
            Toast.makeText(this@RegisterActivity, "잘못된 입력이 존재합니다.", Toast.LENGTH_LONG).show()
        }
    }
}