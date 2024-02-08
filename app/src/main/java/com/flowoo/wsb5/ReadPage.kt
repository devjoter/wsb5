package com.flowoo.wsb5

import FirebaseHandler
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Locale

class ReadPage : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var displayTitleDataTextView: TextView
    private lateinit var displayContentDataTextView: TextView
    private lateinit var firebaseHandler: FirebaseHandler

    private var contentSentences: MutableList<String> = mutableListOf()
    private var currentSentenceIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_page)

        initializeViews()
        initializeTextToSpeech()
        initializeFirebaseHandler()

        // Retrieve data from Firestore
        retrieveDataFairytale()
    }

    private fun initializeViews() {
        displayTitleDataTextView = findViewById(R.id.displayTitleDataTextView)
        displayContentDataTextView = findViewById(R.id.displayContentDataTextView)

        findViewById<ImageButton>(R.id.homeClick).setOnClickListener {
            // Stop text-to-speech before navigating to MainPage
            stopTextToSpeech(0)

            val intent1 = Intent(this, MainPage::class.java)
            startActivity(intent1)
        }

        findViewById<ImageButton>(R.id.starClickPause).setOnClickListener {
            handleStarButtonClick()
        }
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                setupLanguage()
                setupUtteranceProgressListener()

                // Retrieve data from Firestore after TextToSpeech initialization
                retrieveDataFairytale()
            } else {
                Toast.makeText(this, "TextToSpeech initialization failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeFirebaseHandler() {
        firebaseHandler = FirebaseHandler(firestore)
    }

    private fun setupLanguage() {
        val polishLocale = Locale("pl", "PL")
        val langAvailable = textToSpeech.isLanguageAvailable(polishLocale)

        if (langAvailable == TextToSpeech.LANG_AVAILABLE || langAvailable == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
            val result = textToSpeech.setLanguage(polishLocale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Polish language not supported", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Polish language not available on this device", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUtteranceProgressListener() {
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // This method is called when an utterance (a speech) starts
            }

            override fun onDone(utteranceId: String?) {
                // This method is called when an utterance (a speech) is done
                runOnUiThread {
                    handleUtteranceDone()
                }
            }

            override fun onError(utteranceId: String?) {
                // This method is called if there's an error in processing the utterance
            }
        })
    }

    private fun handleUtteranceDone() {
        currentSentenceIndex++

        if (currentSentenceIndex < contentSentences.size) {
            // Display the next sentence after a short delay
            Handler().postDelayed({
                runOnUiThread {
                    displayContentDataTextView.text = contentSentences[currentSentenceIndex]
                }
            }, 200) // Adjust the delay time as needed

            // Speak the next sentence
            textToSpeech.speak(
                contentSentences[currentSentenceIndex],
                TextToSpeech.QUEUE_FLUSH,
                null,
                "utteranceId"
            )
        } else {
            // If no more sentences, you can handle it as needed
        }
    }

    private var isPaused: Boolean = false

    private fun handleStarButtonClick() {
        if (isPaused) {
            // If currently paused, resume
            resumeTextToSpeech()
        } else {
            // If currently playing, pause
            saveStopIndex()
            stopTextToSpeech(currentSentenceIndex)
            Toast.makeText(this, "Pause...", Toast.LENGTH_SHORT).show()
        }

        // Toggle the state
        isPaused = !isPaused
    }


    private fun retrieveDataFairytale() {

        // Get the recognized text from the Intent
        val recognizedText = intent.getStringExtra("documentTitle")

        // Use the recognized text in the query, or use a default value if not found
        val collectionReference = firestore.collection("tales")
        val query: Query = firebaseHandler.buildQuery(collectionReference, "title", recognizedText ?: "misiowe przygody")

        //Toast.makeText(this, recognizedText, Toast.LENGTH_SHORT).show()

        firebaseHandler.retrieveDataForReading(
            query,
            { title, content ->
                displayTitleDataTextView.text = recognizedText

                // Add a period at the end of the content to ensure correct splitting
                val contentWithPeriod = "$content."

                contentSentences.clear()
                contentSentences.addAll(contentWithPeriod.split('.').filter { it.isNotBlank() })

                // Filter out empty or invalid sentences
                contentSentences = contentSentences.filter { it.isNotBlank() && it.isNotEmpty() }.toMutableList()

                currentSentenceIndex = 0

                // Check if there are valid sentences
                if (contentSentences.isNotEmpty()) {
                    displayContentDataTextView.text = contentSentences[currentSentenceIndex]

                    textToSpeech.speak(
                        contentSentences[currentSentenceIndex],
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "utteranceId"
                    )
                    currentSentenceIndex++
                }
            },
            { exception ->
                println("Error retrieving data: $exception")
            }
        )
    }

    override fun onPause() {
        super.onPause()

        // Save the current index when the activity is paused
        saveStopIndex()
        stopTextToSpeech(currentSentenceIndex)
    }

    private fun saveStopIndex() {
        // Save the current index
        // You can use SharedPreferences or any other persistent storage method
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt("stopIndex", currentSentenceIndex)
            apply()
        }
    }

    override fun onResume() {
        super.onResume()

        // Resume reading from the saved index
        resumeTextToSpeech()
    }

    private fun stopTextToSpeech(stopIndex: Int) {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
            // Set the index to the specified position
            currentSentenceIndex = stopIndex
        }
    }

    private fun resumeTextToSpeech() {
        if (currentSentenceIndex < contentSentences.size) {
            // Display the next sentence after a short delay
            Handler().postDelayed({
                runOnUiThread {
                    displayContentDataTextView.text = contentSentences[currentSentenceIndex]
                }
            }, 200) // Adjust the delay time as needed

            // Speak the next sentence
            textToSpeech.speak(
                contentSentences[currentSentenceIndex],
                TextToSpeech.QUEUE_FLUSH,
                null,
                "utteranceId"
            )
        } else {
            // If no more sentences, you can handle it as needed
        }
    }

    override fun onInit(status: Int) {
        // Handle TextToSpeech initialization
    }
    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }

}
