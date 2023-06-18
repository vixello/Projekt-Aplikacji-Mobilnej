package myapp.musicmastery.data.repository

import android.content.ContentResolver
import android.net.Uri
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.data.model.Recording
import myapp.musicmastery.data.model.User
import myapp.musicmastery.util.UIState
import java.util.*

interface RecordingRepository {
    fun getRecording(user: User?, result: (UIState<List<Recording>>) -> Unit)
    // not using return type, because arelady linked the callbacks of the next access listener and failure listener
    // return happens in the listener, not directly from the function
    fun addRecording(recording: Recording, result: (UIState<String>) -> Unit)
    fun updateRecording(recording: Recording, result: (UIState<String>) -> Unit)
    fun deleteRecording(recording: Recording, result: (UIState<String>) -> Unit)
    fun getRecordingForUserInDateRange(user: User?, startDate: Date, endDate: Date, result: (UIState<List<Recording>>) -> Unit)
    fun calculateTotalDurationForUserInPastWeek(user: User?, result: (UIState<String>) -> Unit)
    suspend fun uploadRecording(fileUri: Uri, onResult: (UIState<Uri>)->Unit)

    fun getFileNameFromUri(uri: Uri): String
    fun getAudioFileDuration(fileUri: Uri): Long?
    object ContentResolverWrapper {
        var contentResolver: ContentResolver? = null
    }
}
