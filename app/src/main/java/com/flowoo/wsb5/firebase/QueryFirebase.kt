package com.flowoo.wsb5.firebase

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class QueryFirebase {

    fun getTales(callback: TalesCallback) {
        val db = Firebase.firestore

        val talesCollection = db.collection("tales")

        val talesDocument = talesCollection
            .get()
            .addOnSuccessListener { documents ->
                callback.onTalesReceived(documents.documents)
            }
            .addOnFailureListener { exception ->
                callback.onTalesReceivedFalse(exception)
            }
    }

}