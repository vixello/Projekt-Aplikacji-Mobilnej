package myapp.musicmastery.util

import android.content.ContentResolver
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore

//table names
object FireStoreTables {
    val GOAL = "goal"
//    val RECORDING = "recording"
    val DATE = "date"
    val USER = "user"
    val USER_ID = "user_id"
}

object SharedPrefConst{
    val LOCAL_SHARES_PREF = "local_shared_pref"
    val USER_SESSION = "user_session"
}

object FirebaseStorageConstants{
    val RECORDING = "recording"
    val ROOT_DIRECTORY = "app"
}

object AudioUtils {
    fun getAudioFileDuration(contentResolver: ContentResolver, fileUri: Uri): Long? {
        val filePath = getFileAbsolutePath(contentResolver, fileUri)
        if (filePath != null) {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            return durationString?.toLongOrNull()
        }
        return null
    }

    private fun getFileAbsolutePath(contentResolver: ContentResolver, fileUri: Uri): String? {
        val cursor = contentResolver.query(fileUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val pathIndex = it.getColumnIndex(MediaStore.MediaColumns.DATA)
                if (pathIndex != -1) {
                    return it.getString(pathIndex)
                }
            }
        }
        return null
    }
}