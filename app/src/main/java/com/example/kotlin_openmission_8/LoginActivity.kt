package com.example.kotlin_openmission_8

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin_openmission_8.databinding.LoginBinding
import com.example.kotlin_openmission_8.validator.InputValidator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginBinding
    private var db = FirebaseFirestore.getInstance()
    private var dbUsers = db.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.previousButton.setOnClickListener { finish() }
        binding.finalLoginBtn.setOnClickListener { clickLoginBtn() }
        binding.findIdLoginPage.setOnClickListener { findIdClick() }
        binding.findPwLoginPage.setOnClickListener { findPwdClick() }
    }

    private fun clickLoginBtn() = with(binding) {
        val id: String = loginInputIdEditText.text.toString()
        val pwd: String = loginInputPasswordEditText.text.toString()
        if(noInput(id,pwd)) attemptLogin(id,pwd)
    }

    private fun noInput(id: String, pwd: String): Boolean = with(binding) {
        val result: Int = InputValidator.validatorEmptyField(listOf(id,pwd))
        return when(result){
            0 -> {showError("아이디와 비밀번호를 입력해주세요"); false}
            1 -> {showError("아이디를 입력해주세요"); false}
            2 -> {showError("비밀번호를 입력해주세요"); false}
            else -> true
        }
    }

    private fun showError(message: String){
        binding.loginFailText.text = message
        binding.loginFailText.visibility = View.VISIBLE
    }

    private fun attemptLogin(id: String, pwd: String) {
        dbUsers.whereEqualTo("userID", id).get().addOnCompleteListener { result ->
            val doc = result.result
            if (doc.isEmpty) return@addOnCompleteListener showError("아이디나 비밀번호가 일치하지 않습니다")
            else login(pwd, doc)
        }
    }

    private fun login(pwd: String, doc: QuerySnapshot) {
        doc.forEach { info ->
            val email: String = info.getString("userEmail") ?: return showError("이메일이 잘못되었습니다. 관리자에게 문의해주세요")
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) Log.d("D", "Login Success")
                    else showError("아이디나 비밀번호가 일치하지 않습니다")
                }
        }
    }

    private fun findIdClick(){
        with(binding){
            findIdLoginPage.setOnClickListener {
                startActivity(Intent(this@LoginActivity, FindIdActivity::class.java))
            }
        }
    }
    private fun findPwdClick(){
        with(binding){
            findPwLoginPage.setOnClickListener {
                startActivity(Intent(this@LoginActivity, FindPwdActivity::class.java))
            }
        }
    }
}