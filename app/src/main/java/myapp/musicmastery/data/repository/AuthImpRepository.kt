package myapp.musicmastery.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.register_login.User
import java.util.*

class AuthImpRepository(val auth: FirebaseAuth,
                        val database:FirebaseFirestore): AuthenticationRepository {

    override fun registerUser(
        email: String,
        password: String,
        user: User,
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    user.id = it.result.user?.uid ?: ""

                }
            }
            .addOnFailureListener {

            }
    }

    override fun loginUser(user: User) {
        TODO("Not yet implemented")
    }

    override fun updateUser(user: User) {
        TODO("Not yet implemented")
    }

    /*override*/ fun updateUserInfo(user: User) {
//        val document = database.collection(FireStoreCollection.USER).document(user.id)

    }

}