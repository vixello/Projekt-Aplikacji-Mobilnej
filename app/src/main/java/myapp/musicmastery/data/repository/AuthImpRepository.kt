package myapp.musicmastery.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import myapp.musicmastery.data.model.User
import myapp.musicmastery.util.FireStoreTables
import myapp.musicmastery.util.UIState

class AuthImpRepository(val auth: FirebaseAuth,
                        val database:FirebaseFirestore): AuthenticationRepository {

    override fun registerUser(email: String, password: String, user: User, result: (UIState<String>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() {
                if ( it.isSuccessful){
                    updateUser(user){it->
                        when(it){
                            is UIState.Success ->{
                                result.invoke(
                                    UIState.Success("User register success!")
                                )
                            }
                            is UIState.Failure ->{
                                result.invoke(UIState.Failure(it.error))
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
    override fun loginUser(email: String, password: String, result: (UIState<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{signin ->
                if(signin.isSuccessful){
                    result.invoke(UIState.Success("Login successful!"))
                }
            }
            .addOnFailureListener{
                result.invoke(UIState.Failure("Authentication failed. Check your email and password"))
            }
    }
    override fun forgotPassword(user: User, result: (UIState<String>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateUser(user: User, result: (UIState<String>) -> Unit) {
        val document = database.collection(FireStoreTables.USER).document()
        user.id = document.id
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

}