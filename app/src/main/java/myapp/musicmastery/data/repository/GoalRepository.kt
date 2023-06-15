package myapp.musicmastery.data.repository

import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.data.model.User
import myapp.musicmastery.util.UIState

interface  GoalRepository {

    fun getGoals(user: User?, result: (UIState<List<Goal>>) -> Unit)
    // not using return type, because arelady linked the callbacks of the next access listener and failure listener
    // return happens in the listener, not directly from the function
    fun addGoal(goal: Goal, result: (UIState<String>) -> Unit)
    fun updateGoal(goal: Goal, result: (UIState<String>) -> Unit)
    fun deleteGoal(goal: Goal, result: (UIState<String>) -> Unit)

}