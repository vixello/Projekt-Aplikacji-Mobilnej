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

    private val _logins= MutableLiveData<UIState<String>>()
    //supporting val (zmienna wspomagająca), not mutable data, editable from outside of the viewmodel
    val logins: LiveData<UIState<String>>
        get() = _logins

    private val _forgotPassword= MutableLiveData<UIState<String>>()
    //supporting val (zmienna wspomagająca), not mutable data, editable from outside of the viewmodel
    val forgotPassword: LiveData<UIState<String>>
        get() = _forgotPassword


    fun register(email: String, password: String, username: String, user: User) {
        _registers.value = UIState.Loading
        repository.registerUser(
            email = email,
            password = password,
            username = username,
            user = user
        ){
            _registers.value = it
        }
    }
//    fun giveUsername(username: String): String{
//        _logins.value = UIState.Loading
//        repository.loginUser(
//            email = email,
//            password = password
//        ){
//            _logins.value = it
//        }
//    }
    fun login(email: String, password: String){
        _logins.value = UIState.Loading
        repository.loginUser(
            email = email,
            password = password
        ){
            _logins.value = it
        }
    }

    fun forgotPassword(email: String){
        _forgotPassword.value = UIState.Loading
        repository.forgotPassword(email){
            _forgotPassword.value = it
        }
    }
    fun logout(result: () -> Unit){
        repository.logout(result)
    }
    fun getSession(result: (User?) -> Unit){
        repository.getSession(result)
    }

}