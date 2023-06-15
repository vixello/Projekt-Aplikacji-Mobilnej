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
import myapp.musicmastery.databinding.FragmentRegisterBinding
import myapp.musicmastery.oal.AuthenticationViewModel
import myapp.musicmastery.util.*

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    lateinit var binding: FragmentRegisterBinding
    val viewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.registerButton.setOnClickListener {
            if (validation()){
                viewModel.register(
                    email = binding.email.text.toString(),
                    password = binding.password.text.toString(),
                    username = binding.username.text.toString(),
                    user = getUser()
                )
            }
        }
    }
    fun observer()
    {
        viewModel.registers.observe(viewLifecycleOwner){
            when(it){
                is UIState.Loading -> {
                    binding.registerButton.setText("")
                    binding.registerProgressbar.show()
                }
                is UIState.Success -> {
                    binding.registerButton.setText("REGISTER")
                    binding.registerProgressbar.hide()
                    toast(it.data)
                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                }
                is UIState.Failure -> {
                    binding.registerButton.setText("REGISTER")
                    binding.registerProgressbar.hide()
                    toast(it.error)
                }
            }
        }
    }

    fun getUser(): User {
        return User(
            id = "",
            email = binding.email.text.toString(),
            username = binding.username.text.toString(),
        )
    }

    fun validation(): Boolean {
        var isValid = true

        if (binding.username.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_username))
        }

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
        if (binding.password.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_password))
        }else{
            if (binding.password.text.toString().length < 8){
                isValid = false
                toast(getString(R.string.invalid_password))
            }
        }
        return isValid
    }
}