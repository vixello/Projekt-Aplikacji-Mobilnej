package myapp.musicmastery.oal

import android.app.Application
import android.net.Uri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.data.model.User
import myapp.musicmastery.data.model.Recording
import myapp.musicmastery.data.repository.RecordingRepository
import myapp.musicmastery.util.UIState
import javax.inject.Inject
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context
import android.content.ContentResolver
import android.media.MediaPlayer
import android.provider.OpenableColumns
import androidx.lifecycle.*

@HiltViewModel
class RecordingViewModel @Inject constructor(val repository: RecordingRepository): AndroidViewModel(
    Application()
) {

    //instance of livedata
    private val _recordings = MutableLiveData<UIState<List<Recording>>>()
    //supporting val (zmienna wspomagająca), not mutable data, editable from outside of the viewmodel
    val recording: LiveData<UIState<List<Recording>>>
            get() = _recordings

    private val _addRecording = MutableLiveData<UIState<String>>()
    //supporting val (zmienna wspomagająca), not mutable data, editable from outside of the viewmodel
    val addRecording: LiveData<UIState<String>>
        get() = _addRecording

    private val _deleteRecording = MutableLiveData<UIState<String>>()
    //supporting val (zmienna wspomagająca), not mutable data, editable from outside of the viewmodel
    val deleteRecording: LiveData<UIState<String>>
        get() = _deleteRecording

    private val _updateRecording = MutableLiveData<UIState<String>>()
    //supporting val (zmienna wspomagająca), not mutable data, editable from outside of the viewmodel
    val updateRecording: LiveData<UIState<String>>
        get() = _updateRecording

    private val _currentPlaybackPosition = MutableLiveData<Int>()
    val currentPlaybackPosition: LiveData<Int> get() = _currentPlaybackPosition

    fun updateCurrentPlaybackPosition(position: Int) {
        _currentPlaybackPosition.value = position
    }
    fun getAudioFileDuration(fileUri: Uri): Long? {
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
        val mediaPlayer = MediaPlayer.create(getApplication(), fileUri)
        val duration = mediaPlayer.duration.toLong()
        mediaPlayer.release()
        return duration
    }

    fun uploadRecording(fileUri: Uri, onResult:(UIState<Uri>) ->Unit)
    {
        onResult.invoke(UIState.Loading)
        //because using coroutines
        viewModelScope.launch {
            repository.uploadRecording(fileUri, onResult)
        }
    }
    fun getRecordings(user: User?) {
        _recordings.value = UIState.Loading
        repository.getRecording(user){
            _recordings.value = it
        }
    }

    fun addRecording(recording: Recording) {
        _addRecording.value = UIState.Loading
        repository.addRecording(recording) {
            _addRecording.value = it
        }
    }

    fun deleteRecording(recording: Recording){
        _deleteRecording.value = UIState.Loading
        repository.deleteRecording(recording) {
            _deleteRecording.value = it
        }
    }
    fun updateRecording(recording: Recording) {
        _updateRecording.value = UIState.Loading
        repository.updateRecording(recording) {
            _updateRecording.value = it
        }
    }

    fun calculateTotalDurationForUserInPastWeek(user: User?, onResult:(UIState<String>) ->Unit){
        repository.calculateTotalDurationForUserInPastWeek(user, onResult)
    }

    fun startPlayback(fileUrl: String, onCompletion: () -> Unit) {
        repository.startPlayback(fileUrl, onCompletion)
    }

    fun pausePlayback() {
        repository.pausePlayback()
    }

    fun stopPlayback() {
        repository.stopPlayback()
    }
    fun getFileUrlFromFirebaseStorage(fileName: String, onResult: (UIState<String>) -> Unit){
        repository.getFileUrlFromFirebaseStorage(fileName,onResult)
    }


}