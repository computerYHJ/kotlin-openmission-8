package com.example.kotlin_openmission_8

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin_openmission_8.databinding.LoginBinding
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
    }

    private fun clickLoginBtn() = with(binding) {
        val id: String = loginInputIdEditText.text.toString()
        val pwd: String = loginInputPasswordEditText.text.toString()
        if(noInput(id,pwd) == 3) attemptLogin(id,pwd)
    }

    private fun noInput(id: String, pwd: String): Int = with(binding) {
        if (id.isEmpty() && pwd.isEmpty()) {
            loginFailText.setText("아이디를 입력해주세요"); loginFailText.visibility = View.VISIBLE
            return 2
        } else if (!id.isEmpty() && pwd.isEmpty()) {
            loginFailText.setText("비밀번호를 입력해주세요"); loginFailText.visibility = View.VISIBLE
            return 2
        } else if (id.isEmpty() && !pwd.isEmpty()) {
            loginFailText.text = "아이디를 입력해주세요"; loginFailText.visibility = View.VISIBLE
            return 2
        }
        return 3
    }

    private fun wrongInput() {
        binding.loginFailText.text = "아이디나 비밀번호가 일치하지 않습니다"
        binding.loginFailText.visibility
    }

    private fun emailWrong() {
        binding.loginFailText.text = "이메일이 잘못되었습니다. 관리자에게 문의해주세요"
        binding.loginFailText.visibility
    }

    private fun attemptLogin(id: String, pwd: String) {
        dbUsers.whereEqualTo("userID", id).get().addOnCompleteListener { result ->
            val doc = result.result
            if (doc.isEmpty) return@addOnCompleteListener wrongInput()
            else login(pwd, doc)
        }
    }

    private fun login(pwd: String, doc: QuerySnapshot) {
        doc.forEach { info ->
            val email: String = info.getString("userEmail") ?: return emailWrong()
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) Log.d("D", "Login Success")
                    else wrongInput()
                }
        }
    }

}