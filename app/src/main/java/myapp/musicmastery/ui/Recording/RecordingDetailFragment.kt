package myapp.musicmastery.ui.Recording

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import myapp.musicmastery.R
//import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.data.model.Recording
import myapp.musicmastery.databinding.FragmentRecordingDetailBinding
import myapp.musicmastery.oal.AuthenticationViewModel
import myapp.musicmastery.oal.RecordingViewModel
import myapp.musicmastery.util.UIState
import myapp.musicmastery.util.hide
import myapp.musicmastery.util.show
import myapp.musicmastery.util.toast
import com.github.dhaval2404.imagepicker.ImagePicker

import java.io.File
import java.util.*


@AndroidEntryPoint

class RecordingDetailFragment : Fragment(){

    val viewModel: RecordingViewModel by viewModels()
    val authenticationViewModel: AuthenticationViewModel by viewModels()
    lateinit var binding: FragmentRecordingDetailBinding
    var edit = false
    var recordingObj: Recording? = null
    val recordingUris: MutableList<Uri> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordingDetailBinding.inflate(layoutInflater)
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
        binding.recordingText.layoutParams.height = heightInPixels

        //update/add gaodl
        if(!edit)
        {
            //popraw pozniej bo nie dziala jak nalezy
            binding.recordingText.setOnTouchListener { v, event ->
                if (binding.recordingText.hasFocus()) {
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

            binding.recordingText.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        binding.updateButton.setOnClickListener {
            if(edit) {
                updateRecording()
                findNavController().navigate(R.id.action_recordingDetailFragment_to_recordingListFragment)
            }
            else
            {
                create()
                findNavController().navigate(R.id.action_recordingDetailFragment_to_recordingListFragment)
            }
        }
        binding.addAudio.setOnClickListener {
            // Launch the audio file picker (You can use any audio file picker library here)
            // After the user selects an audio file, you will receive the file path

            // Assuming you have the audio file path in a variable called 'audioFilePath'
//            binding.progressbar.show()

            binding.progressbar.show()
            ImagePicker.with(this)
                //.crop()
                .compress(1024)
//                .galleryOnly()
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
//            // Create a file from the audio file path
//            val audioFile = File(audioFilePath)
//
//            // Generate a unique filename for the audio file in Firebase Storage
//            val audioFileName = "${System.currentTimeMillis()}_${audioFile.name}"
//
//            // Create a reference to the audio file in Firebase Storage
//            val audioStorageRef = stor.child("audio/$audioFileName")
//
//            // Upload the audio file to Firebase Storage
//            val uploadTask = audioStorageRef.putFile(Uri.fromFile(audioFile))
//
//            // Listen for the upload progress and completion
//            uploadTask.addOnProgressListener { taskSnapshot ->
//                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
//                // Update UI with the upload progress if needed
//            }.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    // Audio upload is successful
//                    val downloadUrl = task.result?.storage?.downloadUrl
//                    // Do something with the download URL if needed
//                } else {
//                    // Handle unsuccessful upload
//                }
//            }
        }

    }
    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data!!
            recordingUris.add(fileUri)
            /*adapter.updateList(recordingUris)*/
            binding.progressbar.hide()
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            binding.progressbar.hide()
            toast(ImagePicker.getError(data))
        } else {
            binding.progressbar.hide()
            Log.e(TAG,"Task Cancelled")
        }
    }
    private fun validateInput(): Boolean{
        var valid = true
        if(binding.recordingText.text.toString().isNullOrEmpty())
        {
            valid = false
            toast("Recording description is missing")
        }
        return valid
    }

    private fun onDonePressing(){
        if(recordingUris.isNullOrEmpty()){
            viewModel.uploadRecording(recordingUris.first()){
                it ->
                when(it){
                    is UIState.Loading ->{
                        binding.progressbar.show()
                    }
                    is UIState.Success ->{
                        binding.progressbar.hide()
                    }
                    is UIState.Failure ->{
                        binding.progressbar.hide()
                        toast(it.error)
                    }
                }
            }
        }

    }
    private fun updateRecording(){
        onDonePressing()
        if(validateInput()){
            viewModel.updateRecording(
                Recording(
                name = binding.recordingName.text.toString(),
                id = recordingObj?.id?:"",
                text = binding.recordingText.text.toString(),
                    recording = getRecordingUrls(),
                    date = Date()
            ).apply { authenticationViewModel.getSession {
                this.user_id = it?.id ?: "" } })
        }
        viewModel.updateRecording.observe(viewLifecycleOwner)
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

    private fun getRecordingUrls(): List<String> {
        if(recordingUris.isNullOrEmpty()){
            return recordingUris.map { it.toString() }
        }
        else{
            return recordingObj?.recording ?: arrayListOf()
        }
    }
    private fun create(){
        onDonePressing()
        if(validateInput()){
            viewModel.addRecording(Recording(
                name = binding.recordingName.text.toString(),
                id = recordingObj?.id?:"",
                text = binding.recordingText.text.toString(),
                recording = getRecordingUrls(),
                date = Date()
            ).apply { authenticationViewModel.getSession {
                this.user_id = it?.id ?: "" } })
//                .apply { authenticationViewModel.getSession { this.user_id = it?.id ?: "" } }
        }
        viewModel.recording.observe(viewLifecycleOwner)
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
                    binding.recordingText.isEnabled = false
                    recordingObj = arguments?.getParcelable("recording")
                    binding.recordingName.setText(recordingObj?.name)
                    binding.recordingText.setText(recordingObj?.text)
                    binding.updateButton.hide()

            }
                "create" -> {
                    edit = false
                    binding.updateButton.setText("Create")
                }
                "edit" -> {
                    edit = true
                    recordingObj = arguments?.getParcelable("recording")
                    binding.recordingName.setText(recordingObj?.name)
                    binding.recordingText.setText(recordingObj?.text)
                    binding.updateButton.setText("update")
                }
            }
        }
    }
}