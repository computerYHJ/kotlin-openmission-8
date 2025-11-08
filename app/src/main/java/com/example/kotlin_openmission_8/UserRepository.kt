package com.example.kotlin_openmission_8

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object UserRepository {

    private var db = FirebaseFirestore.getInstance()

    suspend fun checkDupliceate(input: String, fields: String): Int {
        return try {
            val result = db.collection("users")
                .whereEqualTo(fields, input)
                .get()
                .await()//
            when{
                input.isEmpty() -> 2
                result.isEmpty -> 1
                !result.isEmpty -> 3
                else -> 0
            }
        } catch (e: Exception){ 0 }
    }

    suspend fun register(id: String, password: String, email: String){
        val mAuth = FirebaseAuth.getInstance()
        val dbUsers = db.collection("users")
        try{
            mAuth.createUserWithEmailAndPassword(email, password).await()
            dbUsers.add(User(userID = id, userPW = password, userEmail = email)).await()
        } catch (e: Exception) {
            Log.e("REGISTER ERROR", "회원가입이 실패하였습니다.")
        }
    }

    suspend fun attemptLogin(id: String, pwd: String): Int {
        return try {
            val doc = db.collection("users").whereEqualTo("userID", id).get().await()

            if (doc.isEmpty) { 1 }
            else {
                val info = doc.documents.first()
                val email = info.getString("userEmail") ?: return 2

                val result = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pwd).await()

                if (result.user != null) 99
                else 0
            }
        } catch (e: Exception) { 0 }
    }

    suspend fun attemptFindId(name: String, email: String): String{
        return try{
            val doc = db.collection("users")
                .whereEqualTo("userName", name)
                .whereEqualTo("userEmail", email)
                .get().await()

            if (doc.isEmpty) "1"
            else{
                val info = doc.documents.first()
                val id = info.getString("userID") ?: return "2"
                return id
            }
        } catch (e: Exception) {return "0"}
    }

    suspend fun attemptFindPwd(id: String, email: String): Int{
        return try{
            val doc = db.collection("users")
                .whereEqualTo("userID", id)
                .whereEqualTo("userEmail", email)
                .get().await()

            if (doc.isEmpty) 1
            else{
                try{
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
                    return 2
                } catch (e: Exception) {return 0}
            }
        } catch (e: Exception) {return 0}
    }

}