package com.example.project_sns.ui.view.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentSignUpBinding
import com.example.project_sns.ui.util.CheckSignUp
import com.example.project_sns.ui.view.signup.data.FirebaseUserData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val signUpViewModel: SignUpViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        initFragment()
        editTextProcess()



        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initFragment() {

        binding.btnSignUp.setOnClickListener {
            initSignUp()
        }

        binding.ivSignUpBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initSignUp() {
        val name = binding.etSignUpName.text.toString()
        val email = binding.etSignUpEmail.text.toString()
        val password = binding.etSignUpPassword.text.toString()

        val data = FirebaseUserData(
            name = name,
            email = email,
            image = "",
            createdAt = Timestamp.now()
        )

        viewLifecycleOwner.lifecycleScope.launch {
            Toast.makeText(requireContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show()
            signUpViewModel.signUp(
                email = email,
                password = password,
                data = data
            )
            findNavController().popBackStack()
        }
    }
    private fun editTextProcess() {
        binding.etSignUpName.doAfterTextChanged {
            signUpViewModel.checkName(binding.etSignUpName.text.toString(), binding.tvTextNameCheck)
        }
        binding.etSignUpEmail.doAfterTextChanged {
            signUpViewModel.checkEmail(binding.etSignUpEmail.text.toString(), binding.tvTextEmailCheck)
        }
        binding.etSignUpPassword.doAfterTextChanged {
            signUpViewModel.checkPw(binding.etSignUpPassword.text.toString(), binding.tvTextPasswordCheck)
        }
        binding.etSignUpPasswordConfirm.doAfterTextChanged {
            signUpViewModel.checkConfirmPw(
                binding.etSignUpPassword.text.toString(),
                binding.etSignUpPasswordConfirm.text.toString(),
                binding.tvTextPasswordConfirmCheck
            )
        }
    }


//    suspend 처리 통해 createUser 후에 처리 필요 uid 값이 다름
//    private fun createUser(email : String, password : String, data : FirebaseUserData, view: Unit) {
//
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener {
//                if (it.isSuccessful) {
//                    Toast.makeText(requireContext(), "회원가입 성공", Toast.LENGTH_SHORT).show()
//                    setUserData(data)
//                    findNavController().popBackStack()
//                } else {
//                    try {
//                        it.result
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        Toast.makeText(requireContext(), "이미 있는 이메일 형식입니다", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//            }
//    }
//
//    private fun setUserData(data: FirebaseUserData) {
//        firestore.collection("user")
//            .document(data.email)
//            .set(data)
//            .addOnSuccessListener {
//                Log.d("debug_signup", "success")
//            }
//            .addOnFailureListener {
//                Log.d("debug_signup", "fail")
//            }
//    }

}