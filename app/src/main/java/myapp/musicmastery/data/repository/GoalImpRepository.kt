package myapp.musicmastery.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.data.model.User
import myapp.musicmastery.util.FireStoreTables
import myapp.musicmastery.util.UIState
import org.checkerframework.checker.guieffect.qual.UI
import java.util.*
//implementation of goalrep
                    //instance of firestore
class GoalImpRepository(val database:FirebaseFirestore): GoalRepository {

    override fun getGoals(user: User?, result: (UIState<List<Goal>>) -> Unit) {
//        println("NAAAAAAAAAAAAAAAAAA"+user?.id)
//        println("NAAAAAAAAAAAAAAAAAA"+FireStoreTables.USER_ID)

        database.collection(FireStoreTables.GOAL)
            .whereEqualTo(FireStoreTables.USER_ID,user?.id)
            .orderBy(FireStoreTables.DATE, Query.Direction.DESCENDING)
            //Ta metoda pobiera dane z kolekcji Firestore. Zwraca obiekt reprezentujący wynik zapytania.
            .get()
            //Jest to funkcja, która zostanie wywołana, gdy operacja pobierania danych zakończy się pomyślnie.
            // Wewnątrz tego bloku następuje przetwarzanie otrzymanych dokumentów.
            .addOnSuccessListener {
                //Tworzy nową pustą listę, która będzie przechowywać obiekty typu Goal.
                val goals = arrayListOf<Goal>()
                for ( document in it)
                {
                    // Konwertuje otrzymany dokument na obiekt typu Goal.
                    // Wykorzystuje funkcję toObject(), która przekształca dokument
                    // Firestore w obiekt na podstawie podanego typu.
                    //convert document into a recording object
                    val goal = document.toObject(Goal::class.java)
                    //add to recording list
                    goals.add(goal)
                }
                //pas the list to result
                result.invoke(
                    UIState.Success(goals)
                )
            }
            .addOnFailureListener{
                result.invoke(
                    UIState.Failure(it.localizedMessage as String)
                )
            }
    }

    override fun addGoal(goal: Goal, result: (UIState<String>) -> Unit) {
//        val documentuser = database.collection(FireStoreTables.USER).document()
        val document = database.collection(FireStoreTables.GOAL).document()
        goal.id = document.id
//        recording.user_id = documentuser.id
        document
            .set(goal)//pass the object
            .addOnSuccessListener {
                result.invoke(
                    UIState.Success("Goal has been created!")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    UIState.Failure(it.localizedMessage as String)
                )
            }
    }

    override fun deleteGoal(goal: Goal, result: (UIState<String>) -> Unit) {
        val document = database.collection(FireStoreTables.GOAL).document(goal.id)
        document
            .delete()//pass the object
            .addOnSuccessListener {
                result.invoke(
                    UIState.Success("Goal has been deleted")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    UIState.Failure(it.localizedMessage as String)
                )
            }
    }
    override fun updateGoal(goal: Goal, result: (UIState<String>) -> Unit) {
        val document = database.collection(FireStoreTables.GOAL).document(goal.id)
        document
            .set(goal)//pass the object
            .addOnSuccessListener {
                result.invoke(
                    UIState.Success("Goal has been updated!")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    UIState.Failure(it.localizedMessage as String)
                )
            }
    }
}