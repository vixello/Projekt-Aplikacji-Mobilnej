package myapp.musicmastery.data.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import androidx.core.content.ContentProviderCompat.requireContext
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
import java.io.File
import java.util.*

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
            val fileName = getFileNameFromUri(fileUri)
            val uri: Uri = withContext(Dispatchers.IO){
                storageReference
                    .child(fileName)
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
    override fun getFileNameFromUri(uri: Uri): String {
        val filePath = uri.path
        val fileName = filePath?.substringAfterLast("/")

        return fileName ?: ""
    }
    @SuppressLint("Recycle")
    override fun getAudioFileDuration(fileUri: Uri): Long? {
        val contentResolver = RecordingRepository.ContentResolverWrapper.contentResolver
//
//        val retriever = MediaMetadataRetriever()
//        retriever.setDataSource(contentResolver, fileUri)
//
//        val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//        return durationString?.toLongOrNull()
//
//        return null
//        val mediaPlayer = MediaPlayer()
//        try {
//            mediaPlayer.setDataSource(requireContext(), fileUri)
//            mediaPlayer.prepare()
//            val duration = mediaPlayer.duration
//            mediaPlayer.release()
//            return duration.toLong()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            mediaPlayer.release()
//        }
//        return null
//        val mediaPlayer = MediaPlayer.create(context, fileUri)
//        val duration = mediaPlayer.duration.toLong()
//        mediaPlayer.release()
        val duration = null
        return duration
    }

    override fun getRecordingForUserInDateRange(user: User?, startDate: Date, endDate: Date, result: (UIState<List<Recording>>) -> Unit) {
        database.collection(FirebaseStorageConstants.RECORDING)
            .whereEqualTo(FireStoreTables.USER_ID, user?.id)
            .whereGreaterThan(FireStoreTables.DATE, startDate)
            .whereLessThan(FireStoreTables.DATE, endDate)
            .orderBy(FireStoreTables.DATE, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val recordings = arrayListOf<Recording>()
                for (document in querySnapshot.documents) {
                    val recording = document.toObject(Recording::class.java)
                    if (recording != null) {
                        recordings.add(recording)
                    }
                }
                result.invoke(UIState.Success(recordings))
            }
            .addOnFailureListener { exception ->
                result.invoke(UIState.Failure(exception.localizedMessage ?: ""))
            }
    }
    override fun calculateTotalDurationForUserInPastWeek(user: User?, result: (UIState<String>) -> Unit) {


        val currentTime = System.currentTimeMillis()
        val pastWeekTime = currentTime - 7 * 24 * 60 * 60 * 1000 // Past week in milliseconds

        var totalDurationMillis: Long = 0

        // Retrieve the files for the user within the past week
        getRecordingForUserInDateRange(user, Date(pastWeekTime), Date(currentTime)) { it ->
            when (it) {
                is UIState.Success -> {
                    val files = it.data

                    for (file in files) {
                        // Get the duration string from the file
                        val durationString = file.duration

                        // Convert duration from "00:00:00" format to milliseconds
                        val parts = durationString?.split(":")
                        val hours = parts?.get(0)?.toLongOrNull() ?: 0
                        val minutes = parts?.get(1)?.toLongOrNull() ?: 0
                        val seconds = parts?.get(2)?.toLongOrNull() ?: 0
                        val durationMillis = hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000

                        totalDurationMillis += durationMillis

                    }

                    // Convert total duration back to desired format
                    val totalDuration = formatDuration(totalDurationMillis)
                    result.invoke(UIState.Success(totalDuration))

                    // Use the totalDuration as needed
                    println("Total Duration: $totalDuration")
                }
                is UIState.Failure -> {
                    val errorMessage = it.error
                    // Handle the failure case, e.g., display an error message
                }
                else -> {}
            }
        }

//        return "" // Return an initial value or handle the result asynchronously
    }

//    fun formatDuration(durationMillis: Long): String {
//        // Convert duration from milliseconds to desired format, e.g., "HH:mm:ss"
//        val hours = durationMillis / (60 * 60 * 1000)
//        val minutes = (durationMillis % (60 * 60 * 1000)) / (60 * 1000)
//        val seconds = (durationMillis % (60 * 1000)) / 1000
//
//        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
//    }

    private fun formatDuration(durationMillis: Long): String {
        val hours = durationMillis / (60 * 60 * 1000)
        val minutes = (durationMillis % (60 * 60 * 1000)) / (60 * 1000)

        // Format the duration as "10h 40min"
        val formattedDuration = StringBuilder()
        if (hours > 0) {
            formattedDuration.append(hours).append("h ")
        }
        if (minutes > 0) {
            formattedDuration.append(minutes).append("min")
        }

        return formattedDuration.toString()
    }

}