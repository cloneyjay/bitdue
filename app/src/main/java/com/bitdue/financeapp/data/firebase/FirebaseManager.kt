package com.bitdue.financeapp.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Centralized manager for Firebase services
 * Provides access to Firebase Authentication, Firestore, and Storage instances
 */
object FirebaseManager {
    
    /**
     * Firebase Authentication instance
     */
    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    
    /**
     * Cloud Firestore instance
     */
    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    
    /**
     * Firebase Storage instance
     */
    val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    
    /**
     * Check if a user is currently signed in
     */
    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }
    
    /**
     * Get the current user's ID
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    /**
     * Get the current user's email
     */
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
}
