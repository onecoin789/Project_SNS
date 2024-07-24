package com.example.project_sns.ui.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentSignUpBinding
import com.example.project_sns.ui.signup.data.FirebaseUserData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        initFragment()



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initFragment() {

        binding.btnSignUp.setOnClickListener {

            initView()
        }

        binding.ivSignUpBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initView() {
        val name = binding.etSignUpName.text.toString()
        val email = binding.etSignUpEmail.text.toString()
        val password = binding.etSignUpPassword.text.toString()
        val passwordConfirm = binding.etSignUpPasswordConfirm.text.toString()
        val data = FirebaseUserData(
            name = name,
            email = email,
            image = "",
            createdAt = Timestamp.now()
        )

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(requireContext(), "공백이 존재합니다", Toast.LENGTH_SHORT).show()
        } else {
            createUser(email, password, data)
        }
    }

    //suspend 처리 통해 createUser 후에 처리 필요 uid 값이 다름
    private fun createUser(email : String, password : String, data : FirebaseUserData) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "회원가입 성공", Toast.LENGTH_SHORT).show()
                    setUserData(data)
                    findNavController().popBackStack()
                } else {
                    try {
                        it.result
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "이미 있는 이메일 형식입니다", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
    }

    private fun setUserData(data: FirebaseUserData) {
        firestore.collection("user")
            .document(data.email)
            .set(data)
            .addOnSuccessListener {
                Log.d("debug_signup", "success")
            }
            .addOnFailureListener {
                Log.d("debug_signup", "fail")
            }
    }

}