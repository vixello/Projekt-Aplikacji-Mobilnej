package myapp.musicmastery.ui.Goal

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import myapp.musicmastery.R
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.databinding.FragmentGoalDetailBinding
import myapp.musicmastery.oal.AuthenticationViewModel
import myapp.musicmastery.oal.GoalViewModel
import myapp.musicmastery.util.UIState
import myapp.musicmastery.util.hide
import myapp.musicmastery.util.show
import myapp.musicmastery.util.toast
import java.util.*


@AndroidEntryPoint

class GoalDetailFragment : Fragment(){

    val viewModel: GoalViewModel by viewModels()
    val authenticationViewModel: AuthenticationViewModel by viewModels()
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
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update()
        //do poprawy
        val heightInDp = 200
        val density = resources.displayMetrics.density
        val heightInPixels = (heightInDp * density).toInt()
        binding.goalText.layoutParams.height = heightInPixels

        //update/add gaodl
        if(!edit)
        {
            //popraw pozniej bo nie dziala jak nalezy
            binding.goalText.setOnTouchListener { v, event ->
                if (binding.goalText.hasFocus()) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_UP -> {
                            v.parent.requestDisallowInterceptTouchEvent(false)
                            v.performClick()
                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }

// Set the EditText to be scrollable only when the text exceeds the available space
//            binding.goalText.isVerticalScrollBarEnabled = true
//            binding.goalText.maxLines = 3 // Adjust the number of lines based on your desired height
//            binding.goalText.isScrollContainer = true

            binding.goalText.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        binding.updateButton.setOnClickListener {
            if(edit) {
                updateGoal()
                findNavController().navigate(R.id.action_goalDetailFragment_to_goalListFragment)
            }
            else
            {
                create()
                findNavController().navigate(R.id.action_goalDetailFragment_to_goalListFragment)
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
                name = binding.goalName.text.toString(),
                id = goalObj?.id?:"",
                text = binding.goalText.text.toString(),
                date = Date()
            ).apply { authenticationViewModel.getSession {
                this.user_id = it?.id ?: "" } })
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
                name = binding.goalName.text.toString(),
                id = goalObj?.id?:"",
                text = binding.goalText.text.toString(),
                date = Date()
            ).apply { authenticationViewModel.getSession {
//                println("NNNNNNNNNNNNNNN" + this.user_id)
                this.user_id = it?.id ?: "" } })
//                .apply { authenticationViewModel.getSession { this.user_id = it?.id ?: "" } }
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
                    goalObj = arguments?.getParcelable("recording")
                    binding.goalName.setText(goalObj?.name)
                    binding.goalText.setText(goalObj?.text)
                    binding.updateButton.hide()

            }
                "create" -> {
                    edit = false
                    binding.updateButton.setText("Create")
                }
                "edit" -> {
                    edit = true
                    goalObj = arguments?.getParcelable("recording")
                    binding.goalName.setText(goalObj?.name)
                    binding.goalText.setText(goalObj?.text)
                    binding.updateButton.setText("update")
                }
            }
        }
    }
}