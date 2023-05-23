package myapp.musicmastery.Goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.databinding.FragmentGoalDetailBinding
import myapp.musicmastery.oal.GoalViewModel
import myapp.musicmastery.util.UIState
import myapp.musicmastery.util.hide
import myapp.musicmastery.util.show
import myapp.musicmastery.util.toast
import java.util.*

@AndroidEntryPoint

class GoalDetailFragment : Fragment(){

    val viewModel: GoalViewModel by viewModels()
    lateinit var binding: FragmentGoalDetailBinding
    var edit = false
    var goalObj: Goal? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalDetailBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update()
        //update/add gaodl
        binding.updateButton.setOnClickListener {
            if(edit) {
                updateGoal()
            }
            else
            {
                create()
            }
        }

    }

    private fun validateInput(): Boolean{
        var valid = true
        if(binding.goalText.text.toString().isNullOrEmpty())
        {
            valid = false
            toast("Goal description is missing")
        }
        return valid
    }

    private fun updateGoal(){
        if(validateInput()){
            viewModel.updateGoal(Goal(
                id = goalObj?.id?:"",
                text = binding.goalText.text.toString(),
                date = Date()
            ))
        }
        viewModel.updateGoal.observe(viewLifecycleOwner)
        {
            //state = it
            when(it){
                is UIState.Loading ->{
                    binding.progressbar.show()
                    binding.updateButton.text = ""

                }
                is UIState.Success ->{
                    binding.progressbar.hide()
                    binding.updateButton.text = "UPDATE"
                    //if we get the data, set it to the adapter list
//                    toast(it.data.toString())
                }
                is UIState.Failure ->{
                    binding.progressbar.hide()
                    binding.updateButton.text = "UPDATE"

                    toast(it.error)

                }
            }
        }
    }
    private fun create(){
        if(validateInput()){
            viewModel.addGoal(Goal(
                id = "",
                text = binding.goalText.text.toString(),
                date = Date()
            ))
        }
        viewModel.goal.observe(viewLifecycleOwner)
        {
            //state = it
            when(it){
                is UIState.Loading ->{
                    binding.progressbar.show()
                    binding.updateButton.text = ""

                }
                is UIState.Success ->{
                    binding.progressbar.hide()
                    binding.updateButton.text = "CREATE"
                    //if we get the data, set it to the adapter list
//                    toast(it.data.toString())
                }
                is UIState.Failure ->{
                    binding.progressbar.hide()
                    binding.updateButton.text = "CREATE"

                    toast(it.error)

                }
            }
        }
    }

    private fun update(){
        val type = arguments?.getString("type", null)
        type?.let {
            when(it){
                "view" -> {
                    edit = false
                    binding.goalText.isEnabled = false
                    goalObj = arguments?.getParcelable("goal")
                    binding.goalText.setText(goalObj?.text)
                    binding.updateButton.hide()

            }
                "create" -> {
                    edit = false
                    binding.updateButton.setText("Create")
                }
                "edit" -> {
                    edit = true
                    goalObj = arguments?.getParcelable("goal")
                    binding.goalText.setText(goalObj?.text)
                    binding.updateButton.setText("update")
                }
            }
        }
    }
}