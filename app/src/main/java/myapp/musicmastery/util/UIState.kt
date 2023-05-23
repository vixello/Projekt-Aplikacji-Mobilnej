package myapp.musicmastery.util

sealed class UIState<out T> {
    //loading
    //Show the progress bar
    object Loading: UIState<Nothing>()

    //success
    data class Success<out T>(val data: T): UIState<T>()

    //failure
    data class Failure(val error: String): UIState<Nothing>()


}