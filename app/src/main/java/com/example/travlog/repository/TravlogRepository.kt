package com.example.travlog.repository

import android.icu.text.CaseMap.Title
import android.net.Uri
import com.example.travlog.models.Travlogs
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val TRAVLOG_COLLECTION_REF = "travlogs"
const val STORAGE_IMAGES_REF = "images"

class TravlogRepository(){

    fun user() = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    private val travlogRef = Firebase
        .firestore.collection(TRAVLOG_COLLECTION_REF)

    private val storageRef = Firebase.storage.reference.child(STORAGE_IMAGES_REF)

    fun getUserTravlogs(
        userId:String
    ): Flow<Resources<List<Travlogs>>> = callbackFlow{
        var snapshotStateListener:ListenerRegistration? = null
        try {
           snapshotStateListener = travlogRef
               .orderBy("createdOn")
               .whereEqualTo("userId", userId)
               .addSnapshotListener{snapshot, e ->
                   val response = if(snapshot != null){
                       val travlogs = snapshot.toObjects(Travlogs::class.java)
                       Resources.Success(data = travlogs)
                   }else{
                       Resources.Error(throwable = e?.cause)
                   }
                   trySend(response)
               }
        }catch (e:Exception){
            trySend(Resources.Error(e?.cause))
            e.printStackTrace()
        }
        awaitClose{
            snapshotStateListener?.remove()
        }
    }

    fun getTravlog(
        travlogId:String,
        onError:(Throwable?) -> Unit,
        onSuccess:(Travlogs) -> Unit
    ){
        travlogRef
            .document(travlogId)
            .get()
            .addOnSuccessListener {
                it?.toObject(Travlogs::class.java)?.let { it1 -> onSuccess.invoke(it1) }
            }
            .addOnFailureListener{result ->
                onError.invoke(result.cause)
            }
    }

    fun addTravlog(
        userId: String,
        title: String,
        description: String,
        imageUri: Uri,
        createdOn: Timestamp,
        onComplete:(Boolean) -> Unit,
    ){
        val documentId = travlogRef.document().id

        // Upload image to Firebase Storage
        val imageRef = storageRef.child("${documentId}_image")
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val imageDownloadUrl = task.result.toString()

                // Create Travlogs object with image download URL
                val travlog = Travlogs(
                    userId,
                    title,
                    description,
                    imageDownloadUrl,
                    createdOn,
                    documentId = documentId,
                )

                // Add Travlog to Firestore
                travlogRef
                    .document(documentId)
                    .set(travlog)
                    .addOnCompleteListener { result ->
                        onComplete.invoke(result.isSuccessful)
                    }
            } else {
                onComplete.invoke(false)
            }
        }
    }

    fun deleteTravlog(travlogId: String, onComplete: (Boolean) -> Unit){
        travlogRef.document(travlogId)
            .delete()
            .addOnCompleteListener{
                onComplete.invoke(it.isSuccessful)
                val imageRef = storageRef.child("${travlogId}_image")
                imageRef.delete()
            }
    }

    fun updateTravlog(
        title: String,
        description: String,
        travlogId: String,
        onResult:(Boolean) -> Unit
    ){
        val updateData = hashMapOf<String,Any>(
            "title" to title,
            "description" to description
        )
        travlogRef.document(travlogId)
            .update(updateData)
            .addOnCompleteListener{
                onResult(it.isSuccessful)
            }
    }

    fun signOut() = Firebase.auth.signOut()

}

sealed class Resources<T>(
    val data:T? = null,
    val throwable: Throwable? = null,
){
    class Loading<T>:Resources<T>()
    class Success<T>(data: T?):Resources<T>(data = data)
    class Error<T>(throwable: Throwable?):Resources<T>(throwable =  throwable)
}