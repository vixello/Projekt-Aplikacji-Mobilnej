package myapp.musicmastery.oal

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.data.repository.GoalRepository
import myapp.musicmastery.util.UIState
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(val repository: GoalRepository): ViewModel() {

    //instance of livedata
    private val _goals = MutableLiveData<UIState<List<Goal>>>()
    //supporting val (zmienna wspomagająca), not mutable data, editable from outside of the viewmodel
    val goal: LiveData<UIState<List<Goal>>>
            get() = _goals

    private val _addGoal = MutableLiveData<UIState<String>>()
    //supporting val (zmienna wspomagająca), not mutable data, editable from outside of the viewmodel
    val addGoal: LiveData<UIState<String>>
        get() = _addGoal

    private val _deleteGoal = MutableLiveData<UIState<String>>()
    //supporting val (zmienna wspomagająca), not mutable data, editable from outside of the viewmodel
    val deleteGoal: LiveData<UIState<String>>
        get() = _deleteGoal

    private val _updateGoal = MutableLiveData<UIState<String>>()
    //supporting val (zmienna wspomagająca), not mutable data, editable from outside of the viewmodel
    val updateGoal: LiveData<UIState<String>>
        get() = _updateGoal

    fun getGoals() {
        _goals.value = UIState.Loading
        repository.getGoals {
            _goals.value = it
        }
    }

    fun addGoal(goal: Goal) {
        _addGoal.value = UIState.Loading
        repository.addGoal(goal) {
            _addGoal.value = it
        }
    }

    fun deleteGoal(goal: Goal){
        _deleteGoal.value = UIState.Loading
        repository.deleteGoal(goal) {
            _deleteGoal.value = it
        }
    }
    fun updateGoal(goal: Goal) {
        _updateGoal.value = UIState.Loading
        repository.updateGoal(goal) {
            _updateGoal.value = it
        }
    }
}