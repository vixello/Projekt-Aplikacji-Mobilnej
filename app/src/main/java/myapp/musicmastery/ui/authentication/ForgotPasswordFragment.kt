package myapp.musicmastery.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import myapp.musicmastery.R
import myapp.musicmastery.data.model.User
import myapp.musicmastery.databinding.FragmentForgotPasswordBinding
import myapp.musicmastery.oal.AuthenticationViewModel
import myapp.musicmastery.util.*

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    lateinit var binding: FragmentForgotPasswordBinding
    val viewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.forgotButton.setOnClickListener {
            if(validation()){
                viewModel.forgotPassword(binding.email.text.toString())
            }
        }

    }
    fun observer()
    {
        viewModel.forgotPassword.observe(viewLifecycleOwner){
            when(it){
                is UIState.Loading -> {
                    binding.forgotButton.setText("")
                    binding.forgotProgressbar.show()
                }
                is UIState.Success -> {
                    binding.forgotButton.setText("SEND")
                    binding.forgotProgressbar.hide()
                    toast(it.data)
//                    findNavController().navigate(R.id.ac)
                }
                is UIState.Failure -> {
                    binding.forgotButton.setText("SEND")
                    binding.forgotProgressbar.hide()
                    toast(it.error)
                }
            }
        }
    }



    fun validation(): Boolean {
        var isValid = true

        if (binding.email.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_email))
        }
        else{
            if (!binding.email.text.toString().emailValidation()){
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }
        return isValid
    }
}