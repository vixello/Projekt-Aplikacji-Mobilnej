package myapp.musicmastery.register_login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.api.Authentication
import myapp.musicmastery.data.repository.AuthenticationRepository
import javax.inject.Inject

class AuthenticationViewModel  @Inject constructor(
    val repository: AuthenticationRepository
): ViewModel() {
    private val _register = MutableLiveData<String>()
//    val register: LiveData<String>
//        get() = _register
//    fun register(){
//        email: String,
//        password: String.
//        user:User
//    }{
//
//    }

}