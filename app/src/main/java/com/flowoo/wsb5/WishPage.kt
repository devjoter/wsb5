package com.flowoo.wsb5

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import FirebaseHandler
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

import java.util.*

class WishPage : AppCompatActivity() {


    private val SPEECH_REQUEST_CODE = 123


    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var firebaseHandler: FirebaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish_page)

        // Initialize Firebase
        firebaseHandler = FirebaseHandler(firestore)

        // home click button action
        val homeClick: ImageButton = findViewById(R.id.homeClick)
        homeClick.setOnClickListener {
            val intent1 = Intent(this, MainPage::class.java)
            startActivity(intent1)
        }

        // go to read page action on star pressed
        val starClick: ImageButton = findViewById(R.id.starClick)
        starClick.setOnClickListener {
            startSpeechRecognition()
        }
    }

    private fun startSpeechRecognition() {
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pl_PL")

        try {
            startActivityForResult(speechRecognizerIntent, SPEECH_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            // Handle exception when no SpeechRecognizer is found
        }
    }


    private fun handleRecognizedText(text: String) {
        // Split the recognized text into individual words
        val recognizedWords = text.split(" ")

        // Query Firestore to find documents with content containing at least 60% of recognized words
        val collectionReference = firestore.collection("tales")

        collectionReference.get()
            .addOnSuccessListener { documents ->
                // Initialize variables for the best matching document
                var bestMatchDocument: DocumentSnapshot? = null
                var maxMatchPercentage = 0.0

                for (document in documents) {
                    // Access content from the document
                    val content = document.getString("content")

                    if (content != null) {
                        // Split content into individual words
                        val contentWords = content.split(" ")

                        // Calculate the percentage of recognized words in the content
                        val matchPercentage = calculateMatchPercentage(recognizedWords, contentWords)

                        // Check if this document has a higher match percentage
                        if (matchPercentage > maxMatchPercentage) {
                            bestMatchDocument = document
                            maxMatchPercentage = matchPercentage
                        }
                    }
                }

                // Check if a document with sufficient match percentage was found
                if (bestMatchDocument != null && maxMatchPercentage >= 25.0) {
                    // Retrieve additional information or navigate to the desired activity
                    val title = bestMatchDocument.getString("title")
                    val intent = Intent(this, ReadPage::class.java)
                    intent.putExtra("documentTitle", title)
                    startActivity(intent)
                } else {
                    // Handle the case when no suitable document is found
                    Toast.makeText(this, "No suitable document found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
                println("Error querying Firestore: $exception")
            }
    }

    private fun calculateMatchPercentage(recognizedWords: List<String>, contentWords: List<String>): Double {
        // Calculate the percentage of recognized words in the content
        val matchingWords = recognizedWords.intersect(contentWords)
        return (matchingWords.size.toDouble() / recognizedWords.size.toDouble()) * 100.0
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            val spokenText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            // Handle the recognized text
            if (!spokenText.isNullOrEmpty()) {
//                // Start ReadPage with the recognized text as an extra
//                val intent = Intent(this, ReadPage::class.java)
//                intent.putExtra("recognizedText", spokenText)
//                startActivity(intent)
                if (!spokenText.isNullOrEmpty()) {
                    // Call the function to handle the recognized text
                    handleRecognizedText(spokenText)
                }
            }
        }
    }

}