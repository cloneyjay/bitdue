package com.bitdue.financeapp.data.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Manager for Firebase Storage operations
 * Handles file upload, download, and deletion
 */
class FirebaseStorageManager(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    
    private val userId: String?
        get() = auth.currentUser?.uid
    
    /**
     * Get user's storage reference
     */
    private fun getUserStorageRef(): StorageReference? {
        return userId?.let { storage.reference.child("users/$it") }
    }
    
    /**
     * Upload a file to Firebase Storage
     * @param uri Local file URI
     * @param path Remote file path under user's folder
     * @return Flow emitting upload progress (0.0 to 1.0) and final download URL
     */
    fun uploadFile(uri: Uri, path: String): Flow<UploadProgress> = callbackFlow {
        try {
            val storageRef = getUserStorageRef()?.child(path)
                ?: throw IllegalStateException("User not authenticated")
            
            val uploadTask = storageRef.putFile(uri)
            
            // Listen for progress
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                trySend(UploadProgress.InProgress(progress / 100.0))
            }.addOnSuccessListener {
                // Get download URL after successful upload
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    trySend(UploadProgress.Success(downloadUri.toString()))
                    close()
                }.addOnFailureListener { exception ->
                    trySend(UploadProgress.Error(exception.message ?: "Failed to get download URL"))
                    close(exception)
                }
            }.addOnFailureListener { exception ->
                trySend(UploadProgress.Error(exception.message ?: "Upload failed"))
                close(exception)
            }
            
            awaitClose {
                if (uploadTask.isInProgress) {
                    uploadTask.cancel()
                }
            }
        } catch (e: Exception) {
            trySend(UploadProgress.Error(e.message ?: "Upload error"))
            close(e)
        }
    }
    
    /**
     * Upload bytes to Firebase Storage
     * @param bytes Byte array to upload
     * @param path Remote file path under user's folder
     * @return Download URL on success
     */
    suspend fun uploadBytes(bytes: ByteArray, path: String): Result<String> {
        return try {
            val storageRef = getUserStorageRef()?.child(path)
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            storageRef.putBytes(bytes).await()
            val downloadUrl = storageRef.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Download a file from Firebase Storage
     * @param path Remote file path under user's folder
     * @return Byte array of the file contents
     */
    suspend fun downloadFile(path: String): Result<ByteArray> {
        return try {
            val storageRef = getUserStorageRef()?.child(path)
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            val bytes = storageRef.getBytes(Long.MAX_VALUE).await()
            Result.success(bytes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get download URL for a file
     * @param path Remote file path under user's folder
     * @return Download URL string
     */
    suspend fun getDownloadUrl(path: String): Result<String> {
        return try {
            val storageRef = getUserStorageRef()?.child(path)
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            val url = storageRef.downloadUrl.await()
            Result.success(url.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete a file from Firebase Storage
     * @param path Remote file path under user's folder
     */
    suspend fun deleteFile(path: String): Result<Unit> {
        return try {
            val storageRef = getUserStorageRef()?.child(path)
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            storageRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * List all files in a directory
     * @param path Directory path under user's folder
     * @return List of file names
     */
    suspend fun listFiles(path: String): Result<List<String>> {
        return try {
            val storageRef = getUserStorageRef()?.child(path)
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            val listResult = storageRef.listAll().await()
            val fileNames = listResult.items.map { it.name }
            Result.success(fileNames)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Sealed class representing upload progress states
 */
sealed class UploadProgress {
    data class InProgress(val progress: Double) : UploadProgress()
    data class Success(val downloadUrl: String) : UploadProgress()
    data class Error(val message: String) : UploadProgress()
}
