package com.example.project_sns.ui.view.login

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentLoginBinding
import com.example.project_sns.ui.util.CheckLogin
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        initView()

        return binding.root
    }





    private fun initView() {

        binding.tvLoginSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.btnLogin.setOnClickListener {
            initLogin()
            collectFlow()
        }

        binding.ivLoginShowPassword.setOnClickListener {
            binding.ivLoginHidePassword.visibility = View.GONE
            showPassword()

        }
    }

    private fun collectFlow() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                loginViewModel.loginEvent.collect { checkLogin ->
                    when (checkLogin) {
                        is CheckLogin.LoginSuccess -> {
                            Toast.makeText(requireContext(), checkLogin.message, Toast.LENGTH_SHORT)
                                .show()
                            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                        }

                        is CheckLogin.LoginFail -> {
                            Toast.makeText(requireContext(), checkLogin.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun initLogin() {
        val email = binding.etLoginEmail.text.toString()
        val password = binding.etLoginPassword.text.toString()

        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.login(email, password)
        }
    }

    private fun showPassword() {
        val showPw = binding.ivLoginShowPassword
        when (showPw.tag) {
            "0" -> {
                binding.ivLoginShowPassword.tag = "1"
                binding.etLoginPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.ivLoginShowPassword.setImageResource(R.drawable.ic_show)
            }

            "1" -> {
                binding.ivLoginShowPassword.tag = "0"
                binding.etLoginPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.ivLoginShowPassword.setImageResource(R.drawable.ic_hide)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
