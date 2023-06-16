package myapp.musicmastery.data.repository

import android.net.Uri
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.data.model.Recording
import myapp.musicmastery.data.model.User
import myapp.musicmastery.util.FireStoreTables
import myapp.musicmastery.util.FirebaseStorageConstants
import myapp.musicmastery.util.UIState

class RecordingImpRepository(val database: FirebaseFirestore, val storageReference: StorageReference):RecordingRepository {


    override fun getRecording(user: User?, result: (UIState<List<Recording>>) -> Unit) {
        database.collection(FirebaseStorageConstants.RECORDING)
            .whereEqualTo(FireStoreTables.USER_ID,user?.id)
            .orderBy(FireStoreTables.DATE, Query.Direction.DESCENDING)
            //Ta metoda pobiera dane z kolekcji Firestore. Zwraca obiekt reprezentujący wynik zapytania.
            .get()
            //Jest to funkcja, która zostanie wywołana, gdy operacja pobierania danych zakończy się pomyślnie.
            // Wewnątrz tego bloku następuje przetwarzanie otrzymanych dokumentów.
            .addOnSuccessListener {
                //Tworzy nową pustą listę, która będzie przechowywać obiekty typu Recording.
                val recordings = arrayListOf<Recording>()
                for ( document in it)
                {
                    // Konwertuje otrzymany dokument na obiekt typu Recording.
                    // Wykorzystuje funkcję toObject(), która przekształca dokument
                    // Firestore w obiekt na podstawie podanego typu.
                    //convert document into a recording object
                    val recording = document.toObject(Recording::class.java)
                    //add to recording list
                    recordings.add(recording)
                }
                //pas the list to result
                result.invoke(
                    UIState.Success(recordings)
                )
            }
            .addOnFailureListener{
                result.invoke(
                    UIState.Failure(it.localizedMessage as String)
                )
            }
    }

    override fun addRecording(recording: Recording, result: (UIState<String>) -> Unit) {
//        val documentuser = database.collection(FireStoreTables.USER).document()
        val document = database.collection(FirebaseStorageConstants.RECORDING).document()
        recording.id = document.id
//        recording.user_id = documentuser.id
        document
            .set(recording)//pass the object
            .addOnSuccessListener {
                result.invoke(
                    UIState.Success("Recording has been created!")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    UIState.Failure(it.localizedMessage as String)
                )
            }
    }

    override fun deleteRecording(recording: Recording, result: (UIState<String>) -> Unit) {
        val document = database.collection(FirebaseStorageConstants.RECORDING).document(recording.id)
        document
            .delete()//pass the object
            .addOnSuccessListener {
                result.invoke(
                    UIState.Success("Recording has been deleted")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    UIState.Failure(it.localizedMessage as String)
                )
            }
    }
    override fun updateRecording(recording: Recording, result: (UIState<String>) -> Unit) {
        val document = database.collection(FirebaseStorageConstants.RECORDING).document(recording.id)
        document
            .set(recording)//pass the object
            .addOnSuccessListener {
                result.invoke(
                    UIState.Success("Recording has been updated!")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    UIState.Failure(it.localizedMessage as String)
                )
            }
    }
    override suspend fun uploadRecording(fileUri: Uri, onResult: (UIState<Uri>) -> Unit) {
        try{
            val uri: Uri = withContext(Dispatchers.IO){
                storageReference
                    .putFile(fileUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            onResult.invoke(UIState.Success(uri))
        }
        catch( error: FirebaseException){
            error.message?.let { UIState.Failure(it) }?.let { onResult.invoke(it) }
        }
        catch ( error: Exception){
            error.message?.let { UIState.Failure(it) }?.let { onResult.invoke(it) }
        }
    }
}