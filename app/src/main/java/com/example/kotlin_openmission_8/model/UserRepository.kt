package com.example.kotlin_openmission_8.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object UserRepository {
    suspend fun checkDuplicate(input: String, fields: String): DuplicateInput {
        var db = FirebaseFirestore.getInstance()
        return try {
            val result = db.collection("users")
                .whereEqualTo(fields, input)
                .get()
                .await()//
            when {
                result.isEmpty -> DuplicateInput.OK
                !result.isEmpty -> DuplicateInput.ALREADY_INPUT_DATA
                else -> DuplicateInput.UNKNOWN_ERROR
            }
        } catch (e: Exception) {
            DuplicateInput.UNKNOWN_ERROR
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
            Log.e("REGISTER ERROR", "회원가입 실패: ${e.message}")
        }
    }
    suspend fun attemptLogin(id: String, pwd: String): LoginResult {
        var db = FirebaseFirestore.getInstance()
        return try {
            val doc = db.collection("users").whereEqualTo("userID", id).get().await()

            if (doc.isEmpty) {
                LoginResult.ID_WRONG
            } else {
                val info = doc.documents.first()
                val email = info.getString("userEmail") ?: return LoginResult.EMAIL_WRONG
                val result =
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pwd).await()

                if (result.user != null) LoginResult.OK
                else LoginResult.PASSWORD_WRONG
            }
        } catch (e: Exception) {
            LoginResult.PASSWORD_WRONG
        }
    }

    suspend fun attemptFindId(name: String, email: String): FindResult {
        var db = FirebaseFirestore.getInstance()
        return try {
            val doc = db.collection("users")
                .whereEqualTo("userName", name)
                .whereEqualTo("userEmail", email)
                .get().await()

            if (doc.isEmpty) FindResult.WrongInput
            else {
                val info = doc.documents.first()
                val id = info.getString("userID") ?: return FindResult.IdError
                return FindResult.Ok(id)
            }
        } catch (e: Exception) {
            return FindResult.UnknownError
        }
    }

    suspend fun attemptFindPwd(id: String, email: String): FindResult {
        var db = FirebaseFirestore.getInstance()
        return try {
            val doc = db.collection("users")
                .whereEqualTo("userID", id)
                .whereEqualTo("userEmail", email)
                .get().await()

            if (doc.isEmpty) FindResult.IdError
            else {
                try {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
                    return FindResult.Ok(id)
                } catch (e: Exception) {
                    return FindResult.UnknownError
                }
            }
        } catch (e: Exception) {
            return FindResult.UnknownError
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

    suspend fun getUpdateUser(id: String, detail: Map<String, String>, step: Int) {
        val db = FirebaseFirestore.getInstance()
        val doc = db.collection("users").whereEqualTo("userID", id).get().await()

        if (!doc.isEmpty) {
            for (user in doc.documents) {
                when (step) {
                    1 -> db.collection("users").document(user.id).update(detail).await()
                    2 -> db.collection("users").document(user.id)
                        .update("workoutCount", detail["workoutCount"]!!.toInt()).await()
                }

            }
        }
    }
}