package com.example.kotlin_openmission_8

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


object UserRepository {

    suspend fun checkDupliceate(input: String, fields: String): Int {
        var db = FirebaseFirestore.getInstance()
        return try {
            val result = db.collection("users")
                .whereEqualTo(fields, input)
                .get()
                .await()//
            when {
                result.isEmpty -> 1
                !result.isEmpty -> 3
                else -> 0
            }
        } catch (e: Exception) {
            0
        }
    }

    suspend fun register(user: User) {
        var db = FirebaseFirestore.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val dbUsers = db.collection("users")
        try {
            mAuth.createUserWithEmailAndPassword(user.userEmail, user.userPW).await()
            dbUsers.add(user).await()
        } catch (e: Exception) {
            Log.e("REGISTER ERROR", "회원가입이 실패하였습니다.")
        }
    }

    suspend fun attemptLogin(id: String, pwd: String): Int {
        var db = FirebaseFirestore.getInstance()
        return try {
            val doc = db.collection("users").whereEqualTo("userID", id).get().await()

            if (doc.isEmpty) {
                1
            } else {
                val info = doc.documents.first()
                val email = info.getString("userEmail") ?: return 2
                val result =
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pwd).await()

                if (result.user != null) 99
                else 0
            }
        } catch (e: Exception) {
            0
        }
    }

    suspend fun attemptFindId(name: String, email: String): String {
        var db = FirebaseFirestore.getInstance()
        return try {
            val doc = db.collection("users")
                .whereEqualTo("userName", name)
                .whereEqualTo("userEmail", email)
                .get().await()

            if (doc.isEmpty) "1"
            else {
                val info = doc.documents.first()
                val id = info.getString("userID") ?: return "2"
                return id
            }
        } catch (e: Exception) {
            return "0"
        }
    }

    suspend fun attemptFindPwd(id: String, email: String): Int {
        var db = FirebaseFirestore.getInstance()
        return try {
            val doc = db.collection("users")
                .whereEqualTo("userID", id)
                .whereEqualTo("userEmail", email)
                .get().await()

            if (doc.isEmpty) 1
            else {
                try {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
                    return 2
                } catch (e: Exception) {
                    return 0
                }
            }
        } catch (e: Exception) {
            return 0
        }
    }

    suspend fun getLoginUser(id: String): User? {
        val db = FirebaseFirestore.getInstance()
        return try {
            val doc = db.collection("users")
                .whereEqualTo("userID", id)
                .get().await()

            if (doc.isEmpty) {
                null
            } else {
                val user = doc.documents.first()
                user.toObject(User::class.java)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUpdateUser(id: String, detail: Map<String, String>) {
        val db = FirebaseFirestore.getInstance()
        val doc = db.collection("users").whereEqualTo("userID", id).get().await()

        if (!doc.isEmpty) {
            for (user in doc.documents) {
                db.collection("users").document(user.id).update(detail).await()
            }
        }
    }
}