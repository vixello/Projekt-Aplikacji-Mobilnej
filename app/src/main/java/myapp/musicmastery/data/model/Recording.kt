package myapp.musicmastery.data.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize

data class Recording(var name: String ="",
                     val fileName:String = "",
                     var user_id: String = "",
                     var id: String = "",
                     val text: String = "",
                     val duration: String? = "",
                     val recording: List<String> = arrayListOf(), @ServerTimestamp val date: Date = Date()
):Parcelable
