package myapp.musicmastery.Goal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import myapp.musicmastery.R
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.databinding.FragmentGoalListBinding
import myapp.musicmastery.oal.GoalViewModel
import myapp.musicmastery.util.UIState
import myapp.musicmastery.util.hide
import myapp.musicmastery.util.show
import myapp.musicmastery.util.toast

@AndroidEntryPoint

class GoalListFragment : Fragment() {

    val TAG: String = "GoalListFragment"
    lateinit var binding: FragmentGoalListBinding
    val viewModel: GoalViewModel by viewModels()
    val adapter by lazy {
        GoalAdapter(onItemClick = {position, item ->
            findNavController().navigate(R.id.action_goalListFragment_to_goalDetailFragment, Bundle().apply {
                putString("type", "view")
                putParcelable("goal",item)

            })
        }, onEditClick = {position, item ->
            findNavController().navigate(R.id.action_goalListFragment_to_goalDetailFragment, Bundle().apply {
                putString("type", "edit")
                putParcelable("goal",item)
            })
        }, onDeleteClick = {position, item ->

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycleView.adapter = adapter
        binding.createBttn.setOnClickListener {
            findNavController().navigate(R.id.action_goalListFragment_to_goalDetailFragment, Bundle().apply {
                putString("type","create")
            })
        }
        //observe livedata in the fragment
        viewModel.getGoals()
        viewModel.goal.observe(viewLifecycleOwner)
        {
            //state = it
            when(it){
                is UIState.Loading ->{
                    binding.progressbar.show()
                }
                is UIState.Success ->{
                    binding.progressbar.hide()
                    //if we get the data, set it to the adapter list
                    adapter.updateList(it.data.toMutableList())
                }
                is UIState.Failure ->{
                    binding.progressbar.hide()
                    toast(it.error)

                }
            }
        }
    }
}