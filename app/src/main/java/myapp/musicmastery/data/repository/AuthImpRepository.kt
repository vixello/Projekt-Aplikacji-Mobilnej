package myapp.musicmastery.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import myapp.musicmastery.data.model.User
import myapp.musicmastery.util.FireStoreTables
import myapp.musicmastery.util.SharedPrefConst
import myapp.musicmastery.util.UIState

class AuthImpRepository(val auth: FirebaseAuth,
                        val database:FirebaseFirestore,
                        val appPreferences: SharedPreferences,
                        val gson: Gson
): AuthenticationRepository {

    override fun registerUser(email: String, password: String, username: String, user: User, result: (UIState<String>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() {
                if ( it.isSuccessful){
                    user.id = it.result.user?.uid?: ""
                    updateUser(user){state->
                        when(state){
                            is UIState.Success ->{
                                result.invoke(
                                    UIState.Success("User register success!")
                                )
                                storeSession(id = it.result.user?.uid ?: "") {
                                    if (it == null) {
                                        result.invoke(UIState.Failure("User register successfully but session failed to store"))
                                    } else {
                                        result.invoke(
                                            UIState.Success("User register successfull!")
                                        )
                                    }
                                }
                            }
                            is UIState.Failure ->{
                                result.invoke(UIState.Failure(state.error))
                            }
                            else -> {}
                        }
                    }

                }
                else{
                    try {
                        throw it.exception ?: java.lang.Exception("Invalid authentication")
                    }
                    catch (error: FirebaseAuthWeakPasswordException){
                        result.invoke(UIState.Failure("Authentication failed, Password should have at least 6 characters"))
                    }
                    catch (error: FirebaseAuthInvalidCredentialsException){
                        result.invoke(UIState.Failure("Authentication failed, Invalid email"))
                    }
                    catch (error: FirebaseAuthUserCollisionException){
                        result.invoke(UIState.Failure("Authentication failed, Email arleady registered"))
                    }
                    catch (error: Exception){
                        error.message?.let { it1 -> UIState.Failure(it1) }
                            ?.let { it2 -> result.invoke(it2) }
                    }
                }
            }
            .addOnFailureListener{
                result.invoke(
                    UIState.Failure(it.localizedMessage as String)
                )
            }
    }

    override fun storeSession(id: String, result: (User?) -> Unit){
        database.collection(FireStoreTables.USER).document(id)
            .get()
            .addOnCompleteListener{
                if(it.isSuccessful){
                    val user = it.result.toObject(User::class.java)
                    appPreferences.edit().putString(SharedPrefConst.USER_SESSION,gson.toJson(user)).apply()
                    result.invoke(user)
                }
                else{
                    result.invoke(null)
                }
            }
            .addOnFailureListener{
                result.invoke(null)
            }
    }

    override fun getSession(result: (User?) -> Unit) {
        val user_str = appPreferences.getString(SharedPrefConst.USER_SESSION,null)
        if (user_str == null){
            result.invoke(null)
        }else{
            val user = gson.fromJson(user_str,User::class.java)
            result.invoke(user)
        }
    }
    override fun loginUser(email: String, password: String, result: (UIState<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{signin ->
                if(signin.isSuccessful){
                    storeSession(id = signin.result.user?.uid ?: ""){
                        if (it == null){
                            result.invoke(UIState.Failure("Failed to store local session"))
                        }else{
                            result.invoke(UIState.Success("Login successfull!"))
                        }
                    }
                    result.invoke(UIState.Success("Login successful!"))
                }
            }
            .addOnFailureListener{
                result.invoke(UIState.Failure("Authentication failed. Check your email and password"))
            }
    }
    override fun forgotPassword(email: String, result: (UIState<String>) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener{passwordreset ->
                if(passwordreset.isSuccessful){
                    result.invoke(UIState.Success("Email has been sent!!"))
                }
                else{
                    passwordreset.exception?.message?.let { UIState.Failure(it) }
                        ?.let { result.invoke(it) }
                }
            }
            .addOnFailureListener{
                result.invoke(UIState.Failure("Authentication failed. Check your email"))
            }
    }

    override fun updateUser(user: User, result: (UIState<String>) -> Unit) {
        val document = database.collection(FireStoreTables.USER).document(user.id)
//        user.id = document.id
        document
            .set(user)//pass the object
            .addOnSuccessListener {
                result.invoke(
                    UIState.Success("The user has been updated!")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    UIState.Failure(it.localizedMessage as String)
                )
            }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        appPreferences.edit().putString(SharedPrefConst.USER_SESSION,null).apply()
        result.invoke()
    }

    // Retrieve files for a specific user within a date range

}