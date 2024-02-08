import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.random.Random

class FirebaseHandler(private val firestore: FirebaseFirestore) {

    fun retrieveDataForReading(query: Query, onSuccess: (title: String?, content: String?) -> Unit, onFailure: (Exception) -> Unit) {
        query.get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onSuccess(null, null)
                    return@addOnSuccessListener
                }

                val documentList = documents.documents
                val randomIndex = Random.nextInt(0, documentList.size)
                val randomDocument = documentList[randomIndex]

                val title = randomDocument.getString("title")
                val content = randomDocument.getString("content")

                onSuccess(title, content)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun buildQuery(collectionReference: CollectionReference, fieldName: String, value: String): Query {
        return collectionReference.whereEqualTo(fieldName, value)
    }
}
