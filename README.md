initial commit

Baju Baju Overview:
Baju Baju is specifically designed for kids and parents, featuring the integration of SpeechRecognizer and TextToSpeech classes. The application enables users to utilize voice prompts on the WishPage, triggering queries to the Firebase Firestore database. Subsequently, users receive responses containing documents related to their expressed "wishes." The recognized text undergoes processing, and a match is sought within Firestore documents, with a matching accuracy level set at 25%. This accuracy threshold can be fine-tuned as the database accumulates more dynamically generated "Fairy tales."

Future Integration of API for Fairy Tale Generation:
The application's future plan involves implementing an API that will dynamically generate text and images based on speech input. The generated content will be stored in Firestore for future use, allowing users to share their stories or revisit their "likes." However, this feature will be rolled out in stages, as it is not currently available for free. A paid version will be introduced, empowering users to cover the costs associated with API usage, ensuring sustainable and uninterrupted access to this premium functionality. Presently, all text is statically generated, and image generation is not yet implemented in the application.

Development Roadmap:

Current Stage (Free Version): The application provides static text generation and a preliminary version of the Firestore interaction.
Upcoming Stages (Paid Version): Implementation of API-driven dynamic text and image generation, enabling enhanced storytelling capabilities and a broader range of interactive features.


