package myapp.musicmastery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = navHostFragment.navController

//        val user: MutableMap<String, Any> = HashMap()
//        user["frist"] = "Shahzad"
//        user["frist"] = "Afraidi"
//        user["born"] = 1995
//
//        FirebaseFirestore.getInstance().collection("users")
//            .add(user)
//            .addOnSuccessListener { documentReference ->
//                Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.id)
//            }
//            .addOnFailureListener{e -> Log.w("TAG", "Error adding document", e)}
    }
}