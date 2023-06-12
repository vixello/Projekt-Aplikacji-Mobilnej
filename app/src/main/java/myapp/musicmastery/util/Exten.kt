package myapp.musicmastery.util

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.toast(msg: String?){
    Toast.makeText(requireContext(),msg,Toast.LENGTH_LONG).show()
}

//extension of view class
fun View.hide()
{
    visibility = View.GONE
}

fun View.show()
{
    visibility = View.VISIBLE
}

//
fun String.emailValidation() =
    isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

