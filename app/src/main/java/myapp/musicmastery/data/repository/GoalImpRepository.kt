package myapp.musicmastery.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.util.FireStoreTables
import myapp.musicmastery.util.UIState
import org.checkerframework.checker.guieffect.qual.UI
import java.util.*
//implementation of goalrep
                    //instance of firestore
class GoalImpRepository(val database:FirebaseFirestore): GoalRepository {

    override fun getGoals(result: (UIState<List<Goal>>) -> Unit) {
        database.collection(FireStoreTables.GOAL)
            .get()
            .addOnSuccessListener {
                val goals = arrayListOf<Goal>()
                for ( document in it)
                {
                    //convert document into a goal object
                    val goal = document.toObject(Goal::class.java)
                    //add to goal list
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
        val document = database.collection(FireStoreTables.GOAL).document()
        goal.id = document.id
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