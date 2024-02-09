initial commit


Baju Baju is designed for kids and parents. It contain SpeechRecognizer and TextToSpeech
classes. User using voice prompts on WishPage can invoke query to Firebase Firestore database, 
and get response with document containing his "wish". Recognised text is processed and matched with 
firestore documents where word matching is on 25% accuracy level. This can be adjusted as more
generated "Fairy tales" will be present in database.

Intention to generate "Fairy tales" is to use API that will generate text and images 
based on speech input, it will then save current response to firestore for future reuse even
for other Users so they will be able to share stories or come back to "likes"

However this will be implemented in following stages as this feature is not free. There will be 
paid version so the User can cover costs of API usage himself. Therefore at this moment all 
text is generated statically, and image generation is not implemented yet.




