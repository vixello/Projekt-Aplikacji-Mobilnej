package myapp.musicmastery.data.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Goal(var id: String = "", val text: String = "", @ServerTimestamp val date: Date = Date()):Parcelable
