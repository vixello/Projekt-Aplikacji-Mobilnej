


package myapp.musicmastery.ui.Goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import myapp.musicmastery.R
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.databinding.FragmentHomeBinding
import myapp.musicmastery.oal.AuthenticationViewModel
import myapp.musicmastery.oal.GoalViewModel
import myapp.musicmastery.oal.RecordingViewModel
import myapp.musicmastery.util.UIState
import myapp.musicmastery.util.hide
import myapp.musicmastery.util.toast


@AndroidEntryPoint

class HomeFragment : Fragment() {

    val TAG: String = "GoalListFragment"
    lateinit var binding: FragmentHomeBinding
    val authenticationViewModel: AuthenticationViewModel by viewModels()
    var list: MutableList<Goal> = arrayListOf()
    var positionToDelete: Int = -1
    val viewModel: GoalViewModel by viewModels()
    val recordingViewModel: RecordingViewModel by viewModels()
    var goalObj: Goal? = null

    val adapter by lazy {
        GoalAdapter(onItemClick = { position, item ->
            findNavController().navigate(
                R.id.action_goalListFragment_to_goalDetailFragment,
                Bundle().apply {
                    putString("type", "view")
                    putParcelable("goal", item)
                })
        }, onEditClick = {position, item ->
            findNavController().navigate(R.id.action_goalListFragment_to_goalDetailFragment, Bundle().apply {
                putString("type", "edit")
                putParcelable("goal",item)
            })
        }, onDeleteClick = {position, item ->
            positionToDelete = position
            viewModel.deleteGoal(item)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycleView.adapter = adapter
        binding.recycleView.itemAnimator = null

        authenticationViewModel.getSession {
            viewModel.getGoals(it)
        }
        viewModel.goal.observe(viewLifecycleOwner)
        {
            //state = it
            when(it){
                is UIState.Loading ->{
//                    binding.progressbar.show()
                }
                is UIState.Success ->{
//                    binding.progressbar.hide()
                    list = it.data.take(2).toMutableList()
                    //if we get the data, set it to the adapter list
                    adapter.updateList(list)
                }
                is UIState.Failure ->{
//                    binding.progressbar.hide()
                    toast(it.error)

                }
            }
        }
        binding.recycleView.scrollToPosition(0)
        adapter.setVisibility(false)
        authenticationViewModel.getSession { session ->
            // Use the retrieved username here
            binding.userName.text = session?.username

//            println("Username: $username")
        }

        authenticationViewModel.getSession { user ->
            recordingViewModel.calculateTotalDurationForUserInPastWeek(user) {  result ->
                when (result) {
                    is UIState.Success -> {
                        val totalDuration = result.data
                        binding.practiceTime.text = totalDuration
                    }
                    is UIState.Failure -> {
                        val errorMessage = result.error
                        // Handle the failure case, e.g., display an error message
                    }
                    else -> {}
                }

            }
        }
//        println("BOOOOOOOSOSOSOSOSOS$username")
//        for (i in 0 until binding.recycleView.childCount) {
//            val viewHolder =
//                binding.recycleView.findViewHolderForAdapterPosition(i) as? GoalAdapter.ViewHolder
//            viewHolder?.binding?.editButton?.visibility = View.INVISIBLE
//            viewHolder?.binding?.deleteButton?.visibility = View.INVISIBLE
//        }
        // Set the desired layout manager (e.g., LinearLayoutManager)

        binding.seeGoalsButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_goalListFragment)
        }
        binding.userPicture.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_userFragment)
        }

    }

    private fun update(){
        val username = authenticationViewModel.getSession {
            goalObj?.user_id = it?.id ?: "" }
    }

}