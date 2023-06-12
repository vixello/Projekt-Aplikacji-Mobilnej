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
import myapp.musicmastery.databinding.FragmentLoginBinding
import myapp.musicmastery.oal.AuthenticationViewModel
import myapp.musicmastery.util.*

@AndroidEntryPoint
class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    val viewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.loginButton.setOnClickListener {
            if(validation()) {
                viewModel.login(
                    email = binding.email.text.toString(),
                    password = binding.password.text.toString(),
                )
            }
        }
        binding.forgotPassword.setOnClickListener{

        }
        binding.didntRegister.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

    }
    fun observer()
    {
        viewModel.logins.observe(viewLifecycleOwner){
            when(it){
                is UIState.Loading -> {
                    binding.loginButton.setText("")
                    binding.loginProgressbar.show()
                }
                is UIState.Success -> {
                    binding.loginButton.setText("LOGIN")
                    binding.loginProgressbar.hide()
                    toast(it.data)
                    findNavController().navigate(R.id.action_loginFragment_to_goalListFragment)
                }
                is UIState.Failure -> {
                    binding.loginButton.setText("LOGIN")
                    binding.loginProgressbar.hide()
                    toast(it.error)
                }
            }
        }
    }

    fun getUser(): User {
        return User(
            id = "",
            email = binding.email.text.toString()
        )
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