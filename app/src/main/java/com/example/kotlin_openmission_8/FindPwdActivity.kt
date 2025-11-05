package com.example.kotlin_openmission_8

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin_openmission_8.databinding.FindPwdBinding
import com.example.kotlin_openmission_8.validator.InputValidator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FindPwdActivity: AppCompatActivity() {

    private lateinit var binding: FindPwdBinding
    private var db = FirebaseFirestore.getInstance()
    private var dbUsers = db.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FindPwdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.previousButton.setOnClickListener { finish() }
        binding.findPwBtn.setOnClickListener { clickLoginBtn() }
    }

    private fun clickLoginBtn() = with(binding) {
        val id: String = findPwdIdText.text.toString()
        val email: String = findPwdEmailText.text.toString()
        if(noInput(id,email)) attemptFindId(id,email)
    }

    private fun showError(message: String){
        binding.findPwdFailText.text = message
        binding.findPwdFailText.visibility = View.VISIBLE
    }

    private fun printResult(){
        binding.findPwResultLayout.visibility = View.VISIBLE
        binding.findPwResultTextView.text = "비밀번호 재설정 이메일이 전송되었습니다."
    }

    private fun noInput(id: String, email: String): Boolean = with(binding) {
        val result: Int = InputValidator.validatorEmptyField(listOf(id,email))
        return when(result){
            0 -> {showError("아이디와 이메일을 입력해주세요"); false}
            1 -> {showError("아이디를ㅋ 입력해주세요"); false}
            2 -> {showError("이메일을 입력해주세요"); false}
            else -> true
        }
    }

    private fun attemptFindId(id: String, email: String) {
        dbUsers.whereEqualTo("userID", id)
            .whereEqualTo("userEmail", email)
            .get().addOnCompleteListener { result ->
                val doc = result.result
                if (doc.isEmpty) return@addOnCompleteListener showError("아이디나 이메일이 일치하지 않습니다")
                else find(email)
            }
    }

    private fun find(email: String) {
        FirebaseAuth.getInstance()
            .sendPasswordResetEmail(email)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) printResult()
                else showError("아이디나 이메일이 일치하지 않습니다")
            }
    }


}