package myapp.musicmastery.oal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.data.model.User
import myapp.musicmastery.data.repository.AuthenticationRepository
import myapp.musicmastery.util.UIState
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(val repository: AuthenticationRepository): ViewModel() {

    //instance of livedata
    private val _registers = MutableLiveData<UIState<String>>()
    //supporting val (zmienna wspomagająca), not mutable data, editable from outside of the viewmodel
    val registers: LiveData<UIState<String>>
            get() = _registers



    fun register(email: String, password: String, user: User) {
        _registers.value = UIState.Loading
        repository.registerUser(
            email = email,
            password = password,
            user = user
        ){
            _registers.value = it
        }
    }
}