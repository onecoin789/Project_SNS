package com.example.project_sns.ui.view.signup

import android.net.Uri
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentSignUpBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.util.CheckSignUp
import com.example.project_sns.ui.util.dateFormat
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    private val signUpViewModel: SignUpViewModel by viewModels()

    private var uri: Uri? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSignUpBinding {
        return FragmentSignUpBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }


    private fun initView() {

        binding.btnSignUp.setOnClickListener {
            initData()
            collectFlow()
        }

        binding.ivSignUpBack.setOnClickListener {
            backButton()
        }

        binding.ivSignUpShowPassword.setOnClickListener {
            binding.ivSignUpHidePassword.visibility = View.GONE
            showPassword()
        }

        binding.ivSignUpPhotoNull.setOnClickListener {
            uri = null
            binding.ivSignUpPhotoNull.visibility = View.GONE
            binding.ivSignUpPhoto.visibility = View.GONE
        }

        binding.clSignUpPhotoFrame.setOnClickListener {
            getPhoto()
        }

        editTextCheck()

    }

    private fun getPhoto() {
        TedImagePicker.with(requireContext()).start {
            uri = it
            binding.ivSignUpPhoto.clipToOutline = true
            binding.ivSignUpPhotoNull.visibility = View.VISIBLE
            Glide.with(requireContext()).load(it).into(binding.ivSignUpPhoto)
        }
    }


    private fun collectFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                signUpViewModel.signUpEvent.collect { checkSignUp ->
                    when (checkSignUp) {
                        is CheckSignUp.SignUpSuccess -> {
                            Toast.makeText(requireContext(), "회원가입 성공!!", Toast.LENGTH_SHORT).show()
                            backButton()
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