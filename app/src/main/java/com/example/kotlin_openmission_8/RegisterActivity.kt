package com.example.kotlin_openmission_8

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.kotlin_openmission_8.databinding.RegisterBinding
import com.google.android.material.internal.TextWatcherAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: RegisterBinding
    private var db = FirebaseFirestore.getInstance()
    private var dbUsers = db.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.previousButton.setOnClickListener { finish() }

        checkRange(binding.registerIdText, binding.registerIdMessageText, "아이디", 6, 15)
        binding.registerIdDuplicateBtn.setOnClickListener { duplicateIdBtn() }

        checkRange(binding.registerPwdText, binding.registerPwdMessage, "비밀번호", 8, 20)
        notEqualPwd()
    }

    private fun findWrongInput(input: String, textView: TextView): Boolean {
        return if (Regex("""[^a-zA-Z0-9]""").containsMatchIn(input)) {
            resisterMessagePrint(textView, "형식에 맞는 입력이 아닙니다.", Color.RED)
            true
        } else {
            false
        }
    }

    private fun duplicateIdBtn() {
        var id: String = binding.registerIdText.text.toString()
        idDuplicate(id)
    }

    private fun idDuplicate(id: String) {
        dbUsers.whereEqualTo("userID", id).get().addOnCompleteListener { result ->
            if (result.isSuccessful) idMessageResult(result.result)
        }
    }

    private fun idMessageResult(item: QuerySnapshot) = with(binding) {
        when {
            !item.isEmpty -> resisterMessagePrint(
                registerIdMessageText,
                "사용할 수 없는 아이디입니다.",
                Color.RED
            )

            else -> resisterMessagePrint(registerIdMessageText, "사용할 수 있는 아이디입니다.", Color.BLUE)
        }
    }


    private fun resisterMessagePrint(view: TextView, message: String, color: Int) = with(binding) {
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
            textView.text = msg
            textView.visibility = View.VISIBLE
        }
    }

    private fun notEqualPwd() = with(binding) {
        registerSamePwdText.onFocusChangeListener = View.OnFocusChangeListener { view, focus ->
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
        } else {
            if (samePwd.isEmpty()) {
                resisterMessagePrint(registerSamePwdMsg, "비밀번호를 다시 입력하세요.", Color.RED)
            } else if (pwd != samePwd) {
                resisterMessagePrint(registerSamePwdMsg, "비밀번호가 일치하지 않습니다.", Color.RED)
            } else {
                resisterMessagePrint(registerSamePwdMsg, "", Color.RED)
            }
        }
    }



}