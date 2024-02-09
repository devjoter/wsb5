package com.flowoo.wsb5.firebase

import com.google.firebase.firestore.DocumentSnapshot
import java.lang.Exception

interface TalesCallback {
    fun onTalesReceived(documents: List<DocumentSnapshot>)
    fun onTalesReceivedFalse(exception: Exception)
}