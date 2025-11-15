package com.childprotectionsystems.trackerpro.utils

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.tasks.await

object FcmTokenManager {
    private const val TAG = "FcmTokenManager"

    suspend fun saveFcmToken(uid: String) {
        try {
            val token = Firebase.messaging.token.await()
            if (token != null) {
                val userDocRef = Firebase.firestore.collection("users").document(uid)
                val tokenData = mapOf("fcmToken" to token)
                userDocRef.set(tokenData, com.google.firebase.firestore.SetOptions.merge()).await()
            }
        } catch (e: Exception) {
            // Handle exception
        }
    }
}
