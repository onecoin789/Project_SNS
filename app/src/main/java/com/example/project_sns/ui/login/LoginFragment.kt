package com.example.project_sns.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.tvLoginSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.btnLogin.setOnClickListener {
            initLogin()
        }

        return binding.root
    }

    private fun initLogin() {
        val auth = FirebaseAuth.getInstance()
        val email = binding.etLoginEmail.text.toString()
        val password = binding.etLoginPassword.text.toString()
        val user = auth.currentUser?.email

        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(requireContext(), "이메일과 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
        } else if (email.isNotEmpty() || password.isNotEmpty()) {

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    try {
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "환영합니다 ${user}님!", Toast.LENGTH_SHORT)
                                .show()
                            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)

                        } else {
                            Toast.makeText(requireContext(), "로그인 실패", Toast.LENGTH_SHORT).show()

                        }
                    } catch (e: Exception) {
                        Log.d("debug_login", "로그인 에러")
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
