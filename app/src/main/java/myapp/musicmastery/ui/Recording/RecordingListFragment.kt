package myapp.musicmastery.ui.Recording

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import myapp.musicmastery.R
import myapp.musicmastery.data.model.Recording
import myapp.musicmastery.databinding.FragmentRecordingListBinding
import myapp.musicmastery.oal.AuthenticationViewModel
import myapp.musicmastery.oal.RecordingViewModel
import myapp.musicmastery.util.UIState
import myapp.musicmastery.util.hide
import myapp.musicmastery.util.show
import myapp.musicmastery.util.toast


@AndroidEntryPoint

class RecordingListFragment : Fragment() {

    val TAG: String = "RecordingListFragment"
    lateinit var binding: FragmentRecordingListBinding
    var positionToDelete: Int = -1
    var list: MutableList<Recording> = arrayListOf()
    val viewModel: RecordingViewModel by viewModels()
    val authenticationViewModel: AuthenticationViewModel by viewModels()
    val adapter by lazy {
        RecordingAdapter(onItemClick = {position, item ->
            findNavController().navigate(R.id.action_recordingListFragment_to_recordingDetailFragment, Bundle().apply {
                putString("type", "view")
                putParcelable("recording",item)
            })
        }, onEditClick = {position, item ->
            findNavController().navigate(R.id.action_recordingListFragment_to_recordingDetailFragment, Bundle().apply {
                putString("type", "edit")
                putParcelable("recording",item)
            })
        }, onDeleteClick = {position, item ->
            positionToDelete = position
            viewModel.deleteRecording(item)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordingListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycleView.adapter = adapter
        binding.recycleView.itemAnimator = null
        binding.createBttn.setOnClickListener {
            findNavController().navigate(R.id.action_recordingListFragment_to_recordingDetailFragment, Bundle().apply {
                putString("type","create")
            })
        }
        //observe livedata in the fragment
        authenticationViewModel.getSession {
            viewModel.getRecordings(it)
        }
        adapter.setVisibility(true)
        viewModel.recording.observe(viewLifecycleOwner)
        {
            //state = it
            when(it){
                is UIState.Loading ->{
                    binding.progressbar.show()
                }
                is UIState.Success ->{
                    binding.progressbar.hide()
                    list = it.data.toMutableList()
                    //if we get the data, set it to the adapter list
                    adapter.updateList(list)
                }
                is UIState.Failure ->{
                    binding.progressbar.hide()
                    toast(it.error)

                }
            }
        }
        viewModel.deleteRecording.observe(viewLifecycleOwner)
        {
            //state = it
            when(it){
                is UIState.Loading ->{
                    binding.progressbar.show()
                }
                is UIState.Success ->{
                    binding.progressbar.hide()
                    toast(it.data)
                    //if we get the data, set it to the adapter list
//                    if(positionToDelete != -1){
//                        list.removeAt(positionToDelete)
//                        adapter.updateList(list)
                    adapter.deleteItem(positionToDelete)
//                    }
                }
                is UIState.Failure ->{
                    binding.progressbar.hide()
                    toast(it.error)

                }
            }
        }

    }
}