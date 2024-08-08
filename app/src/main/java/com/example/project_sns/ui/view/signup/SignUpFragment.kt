package com.example.project_sns.ui.view.signup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentSignUpBinding
import com.example.project_sns.ui.util.CheckSignUp
import com.example.project_sns.ui.util.dateFormat
import com.example.project_sns.ui.view.signup.model.FirebaseUserData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val signUpViewModel: SignUpViewModel by viewModels()

    private var uri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        initView()


        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initView() {

        binding.btnSignUp.setOnClickListener {
            initData()
            collectFlow()
        }

        binding.ivSignUpBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivSignUpShowPassword.setOnClickListener {
            binding.ivSignUpHidePassword.visibility = View.GONE
            showPassword()
        }

        editTextCheck()
        getPhoto()
    }

    private fun getPhoto() {

        val registerForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    Activity.RESULT_OK -> {
                        uri = result.data?.data!!
                        binding.ivSignUpPhoto.clipToOutline = true
                        Glide.with(requireContext()).load(uri).into(binding.ivSignUpPhoto)
                    }
                }
            }

        binding.clSignUpPhotoFrame.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            registerForActivityResult.launch(intent)
        }
    }

    private fun collectFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                signUpViewModel.signUpEvent.collect { checkSignUp ->
                    when (checkSignUp) {
                        is CheckSignUp.SignUpSuccess -> {
                            Toast.makeText(requireContext(), "회원가입 성공!!", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }

                        is CheckSignUp.SignUpFail -> {
                            Toast.makeText(
                                requireContext(),
                                checkSignUp.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun initData() {

        val time = LocalDateTime.now()

        val name = binding.etSignUpName.text.toString()
        val email = binding.etSignUpEmail.text.toString()
        val password = binding.etSignUpPassword.text.toString()
        val passwordConfirm = binding.etSignUpPasswordConfirm.text.toString()

        val nameCheck = binding.tvTextNameCheck
        val emailCheck = binding.tvTextEmailCheck
        val passwordCheck = binding.tvTextPasswordCheck
        val passwordConfirmCheck = binding.tvTextPasswordConfirmCheck

        viewLifecycleOwner.lifecycleScope.launch {
            if (uri != null) {
                signUpViewModel.checkSignUp(
                    name = name,
                    email = email,
                    password = password,
                    profileImage = uri.toString(),
                    confirmPw = passwordConfirm,
                    createdAt = dateFormat(time),
                    nameCheck = nameCheck,
                    emailCheck = emailCheck,
                    passwordCheck = passwordCheck,
                    confirmCheck = passwordConfirmCheck
                )
            } else {
                signUpViewModel.checkSignUp(
                    name = name,
                    email = email,
                    password = password,
                    profileImage = null,
                    confirmPw = passwordConfirm,
                    createdAt = dateFormat(time),
                    nameCheck = nameCheck,
                    emailCheck = emailCheck,
                    passwordCheck = passwordCheck,
                    confirmCheck = passwordConfirmCheck
                )
            }
        }
    }

    private fun editTextCheck() {
        binding.etSignUpName.doAfterTextChanged {
            signUpViewModel.checkName(binding.etSignUpName.text.toString(), binding.tvTextNameCheck)
        }
        binding.etSignUpEmail.doAfterTextChanged {
            signUpViewModel.checkEmail(
                binding.etSignUpEmail.text.toString(),
                binding.tvTextEmailCheck
            )
        }
        binding.etSignUpPassword.doAfterTextChanged {
            signUpViewModel.checkPw(
                binding.etSignUpPassword.text.toString(),
                binding.tvTextPasswordCheck
            )
        }
        binding.etSignUpPasswordConfirm.doAfterTextChanged {
            signUpViewModel.checkConfirmPw(
                binding.etSignUpPassword.text.toString(),
                binding.etSignUpPasswordConfirm.text.toString(),
                binding.tvTextPasswordConfirmCheck
            )
        }
    }

    private fun showPassword() {
        val showPw = binding.ivSignUpShowPassword
        when (showPw.tag) {
            "0" -> {
                binding.ivSignUpShowPassword.tag = "1"
                binding.etSignUpPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.ivSignUpShowPassword.setImageResource(R.drawable.ic_show)
            }

            "1" -> {
                binding.ivSignUpShowPassword.tag = "0"
                binding.etSignUpPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.ivSignUpShowPassword.setImageResource(R.drawable.ic_hide)
            }
        }
    }

}