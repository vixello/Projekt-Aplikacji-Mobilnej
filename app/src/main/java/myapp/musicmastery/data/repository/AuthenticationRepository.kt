package myapp.musicmastery.data.repository

import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.register_login.User

interface AuthenticationRepository {

    fun registerUser(email:String,password: String, user: User)
    fun loginUser(user: User)
    fun updateUser(user:User)
}