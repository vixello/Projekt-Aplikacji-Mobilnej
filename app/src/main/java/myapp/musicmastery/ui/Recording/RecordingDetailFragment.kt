package myapp.musicmastery.ui.Recording

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.github.dhaval2404.imagepicker.ImagePicker
import android.Manifest;
import android.content.ContentResolver
import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.provider.MediaStore
import android.provider.OpenableColumns
import myapp.musicmastery.data.repository.RecordingRepository
import myapp.musicmastery.data.repository.RecordingRepository.ContentResolverWrapper.contentResolver
import myapp.musicmastery.util.*

import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@AndroidEntryPoint

class RecordingDetailFragment : Fragment(){

    val viewModel: RecordingViewModel by viewModels()
    val authenticationViewModel: AuthenticationViewModel by viewModels()
    lateinit var binding: FragmentRecordingDetailBinding
    var edit = false
    var recordingObj: Recording? = null
    val recordingUris: MutableList<Uri> = arrayListOf()
    private lateinit var contentResolver: ContentResolver

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contentResolver = requireContext().contentResolver
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted, proceed with file picking logic
                pickFile()
            } else {
                toast("permission not granted")
                // Permission is not granted, handle accordingly
                // You can show a toast or an error message to the user
            }
        }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    handlePickedFile(uri)
                    println("NOOOOOOOO$uri")

                }
            }
        }

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
                pickFile()
        }

    }
/*    private fun isReadStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            return result == PackageManager.PERMISSION_GRANTED
        }
        return false
    }

    private fun requestReadStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }*/

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/*" // You can specify the specific audio MIME type you want to allow
        }
        filePickerLauncher.launch(intent)
    }

    @SuppressLint("Range")
    private fun handlePickedFile(uri: Uri) {
        // Access the picked file using the URI
        // You can perform operations on the file (e.g., read its content, save it, etc.)
        recordingUris.add(uri)
//        toast("URL: $recordingUris")

        // Example: Get the file name
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        cursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                // Use the file name as needed
            }
        }
    }
    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = context?.contentResolver?.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val fileName = it.getString(nameIndex)
                return fileName ?: ""
            }
        }
        return ""
    }

     fun getAudioFileDuration(fileUri: Uri): Long? {
        val contentResolver = RecordingRepository.ContentResolverWrapper.contentResolver
        val mediaPlayer = MediaPlayer.create(context, fileUri)
        val duration = mediaPlayer.duration.toLong()
        mediaPlayer.release()
        return duration
    }
    fun formatDuration(duration: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(duration)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60

        println("BAAAAAAA$String.format(\"%02d:%02d:%02d\", hours, minutes, seconds)")
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
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
                    is UIState.Success -> {
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
            val recordingName = recordingUris.firstOrNull()?.let { getFileNameFromUri(it) }
            val duration = recordingUris.firstOrNull()?.let { getAudioFileDuration(it)?.let { it1 ->
                formatDuration(
                    it1
                )
            } }
            if (duration != null) {
                // Do something with the duration
                println("Audio duration: $duration milliseconds")
            } else {
                // Handle the case when duration is null
                println("Unable to retrieve audio duration")
            }
            viewModel.updateRecording(
                Recording(
                    name = binding.recordingName.text.toString(),
                    fileName = recordingName.toString(),
                    id = recordingObj?.id?:"",
                    text = binding.recordingText.text.toString(),
                    duration = duration,
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
        if(recordingUris.isNotEmpty()){
            return recordingUris.map { it.toString() }
        }
        else{
            return recordingObj?.recording ?: arrayListOf()
        }
    }

    private fun create(){
        onDonePressing()
        if(validateInput()){
//            val duration = AudioUtils.getAudioFileDuration(contentResolver, fileUri)
            val recordingName = recordingUris.firstOrNull()?.let { getFileNameFromUri(it) }
            val duration = recordingUris.firstOrNull()?.let { getAudioFileDuration(it)?.let { it1 ->
                formatDuration(
                    it1
                )
            } }
            if (duration != null) {
                // Do something with the duration
                println("NANABABA Audio duration: $duration milliseconds")
            } else {
                // Handle the case when duration is null
                println("NANABABA Unable to retrieve audio duration")
            }

            binding.audioName.text = recordingName
            viewModel.addRecording(Recording(
                name = binding.recordingName.text.toString(),
                fileName = recordingName.toString(),
                id = recordingObj?.id?:"",
                text = binding.recordingText.text.toString(),
                duration = duration,
                recording = getRecordingUrls(),
                date = Date()
            ).apply { authenticationViewModel.getSession {
                this.user_id = it?.id ?: "" } })
//                .apply { authenticationViewModel.getSession { this.user_id = it?.id ?: "" } }
        }
                println("NANNANANAANANNAANAN"+ getRecordingUrls())

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
                    binding.audioName.setText(recordingObj?.fileName)
                    binding.duration.setText(recordingObj?.duration)
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
                    binding.audioName.setText(recordingObj?.fileName)
                    binding.duration.setText(recordingObj?.duration)
                    binding.updateButton.setText("update")
                }
            }
        }
    }



}