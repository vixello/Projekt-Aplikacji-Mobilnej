


package myapp.musicmastery.ui.Goal

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
import myapp.musicmastery.data.model.User
import myapp.musicmastery.databinding.FragmentGoalListBinding
import myapp.musicmastery.databinding.FragmentLearnDetailBinding
import myapp.musicmastery.databinding.FragmentUserBinding
import myapp.musicmastery.oal.AuthenticationViewModel
import myapp.musicmastery.util.UIState
import myapp.musicmastery.util.hide
import myapp.musicmastery.util.show
import myapp.musicmastery.util.toast


@AndroidEntryPoint

class LearnDetailFragment : Fragment() {

    val TAG: String = "GoalListFragment"
    lateinit var binding: FragmentLearnDetailBinding
    val authenticationViewModel: AuthenticationViewModel by viewModels()
    val viewModel: AuthenticationViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLearnDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.logoutButton.setOnClickListener {
//            authenticationViewModel.logout {
//                findNavController().navigate(R.id.action_userFragment_to_loginFragment)
//            }
//        }
    }

}