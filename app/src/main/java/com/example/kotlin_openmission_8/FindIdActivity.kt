package com.example.kotlin_openmission_8

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin_openmission_8.databinding.FindIdBinding
import com.example.kotlin_openmission_8.validator.InputValidator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class FindIdActivity: AppCompatActivity() {

    private lateinit var binding: FindIdBinding
    private var db = FirebaseFirestore.getInstance()
    private var dbUsers = db.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FindIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.previousButton.setOnClickListener { finish() }
        binding.findIdBtn.setOnClickListener { clickLoginBtn() }

    }

    private fun clickLoginBtn() = with(binding) {
        val name: String = findIdNameText.text.toString()
        val email: String = findIdEmailText.text.toString()
        if(noInput(name,email)) attemptFindId(name,email)
    }

    private fun showError(message: String){
        binding.findIdFailText.text = message
        binding.findIdFailText.visibility = View.VISIBLE
    }

    private fun noInput(name: String, email: String): Boolean = with(binding) {
        val result: Int = InputValidator.validatorEmptyField(listOf(name,email))
        return when(result){
            0 -> {showError("이름과 이메일을 입력해주세요"); false}
            1 -> {showError("이름을 입력해주세요"); false}
            2 -> {showError("이메일을 입력해주세요"); false}
            else -> true
        }
    }

    private fun attemptFindId(name: String, email: String) {
        dbUsers.whereEqualTo("userName", name)
            .whereEqualTo("userEmail", email)
            .get().addOnCompleteListener { result ->
            val doc = result.result
            if (doc.isEmpty) return@addOnCompleteListener showError("이름이나 이메일이 일치하지 않습니다")
            else find(doc)
        }
    }

    private fun find(doc: QuerySnapshot) {
        doc.forEach { info ->
            val id: String = info.getString("userID") ?: return showError("아이디가 잘못되었습니다. 관리자에게 문의해주세요")
            binding.findIdResultLayout.visibility = View.VISIBLE
            binding.findIdResultTextView.text = id
            binding.findIdFailText.visibility = View.INVISIBLE
            binding.findIdBtn.text = "로그인 화면 이동"
            binding.findIdBtn.setOnClickListener { finish() }
        }
    }



}