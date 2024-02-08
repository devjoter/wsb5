import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.Locale

class TextToSpeechHandler(private val context: Context) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech = TextToSpeech(context, this)

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Check and set the Polish language
            val polishLocale = Locale("pl", "PL")
            val langAvailable = textToSpeech.isLanguageAvailable(polishLocale)

            if (langAvailable == TextToSpeech.LANG_AVAILABLE || langAvailable == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                val result = textToSpeech.setLanguage(polishLocale)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    showToast("Polish language not supported")
                }
            } else {
                showToast("Polish language not available on this device")
            }
        } else {
            showToast("TextToSpeech initialization failed")
        }
    }

    fun speak(text: String?) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun stop() {
        textToSpeech.stop()
    }

    fun shutdown() {
        textToSpeech.shutdown()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
