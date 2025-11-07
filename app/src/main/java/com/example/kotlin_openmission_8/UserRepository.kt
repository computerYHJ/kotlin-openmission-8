package com.example.kotlin_openmission_8

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
}