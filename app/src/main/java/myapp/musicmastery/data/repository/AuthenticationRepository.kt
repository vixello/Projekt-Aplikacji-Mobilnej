package myapp.musicmastery.data.repository

import myapp.musicmastery.data.model.User
import myapp.musicmastery.util.UIState

interface AuthenticationRepository {

    fun registerUser(email: String, password: String, username: String, user: User, result: (UIState<String>) -> Unit)
    fun loginUser(email: String, password: String, result: (UIState<String>) -> Unit)
    fun forgotPassword(email: String, result: (UIState<String>) -> Unit)
    fun updateUser(user: User, result: (UIState<String>) -> Unit)
    fun logout(result: () -> Unit)
    fun getSession( result: (User?) -> Unit)
    fun storeSession(id: String, result: (User?) -> Unit)

}