package myapp.musicmastery.oal

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.data.model.User
import myapp.musicmastery.data.model.Recording
import myapp.musicmastery.data.repository.RecordingRepository
import myapp.musicmastery.util.UIState
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(val repository: RecordingRepository): ViewModel() {

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
}