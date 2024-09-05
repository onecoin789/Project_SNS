package com.example.project_sns.ui.view.main.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.project_sns.databinding.FragmentMyProfileEditBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentPost
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.util.CheckEditProfile
import com.example.project_sns.ui.view.main.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyProfileEditFragment : BaseFragment<FragmentMyProfileEditBinding>() {

    private val mainSharedViewModel: MainSharedViewModel by viewModels()

    private var uri: Uri? = null


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyProfileEditBinding {
        return FragmentMyProfileEditBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    private fun initView() {

        val userData = CurrentUser.userData

        binding.ivEditBack.setOnClickListener {
            findNavController().popBackStack()
        }
        editTextCheck()

        binding.btnEditConfirm.setOnClickListener {
            initData()
            collectFlow()
        }

        binding.clEditPhotoFrame.setOnClickListener {
            getPhoto()
        }

        if (userData != null) {
            Glide.with(requireContext()).load(userData.profileImage).into(binding.ivEditPhoto)
            binding.etEditName.setText(userData.name)
            binding.etEditEmail.setText(userData.email)
            binding.etEditIntro.setText(userData.intro)
        }
    }

    private fun getPhoto() {

        TedImagePicker.with(requireContext()).start {
            uri = it
            binding.ivEditPhoto.clipToOutline = true
            Glide.with(requireContext()).load(it).into(binding.ivEditPhoto)
        }
    }

    private fun collectFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                mainSharedViewModel.editEvent.collect { checkEdit ->
                    when (checkEdit) {
                        is CheckEditProfile.EditSuccess -> {
                            Toast.makeText(requireContext(), "프로필 수정이 완료되었습니다", Toast.LENGTH_SHORT)
                                .show()
                            findNavController().popBackStack()
                        }

                        is CheckEditProfile.EditFail -> {
                            Toast.makeText(
                                requireContext(),
                                checkEdit.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            Log.d("error_edit", "MyProfileEditFragment")
                        }
                    }
                }
            }
        }
    }

    private fun initData() {
        val uid = CurrentUser.userData?.uid.toString()
        val beforeProfile = CurrentUser.userData?.profileImage
        val name = binding.etEditName.text.toString()
        val intro = binding.etEditIntro.text.toString()
        val email = CurrentUser.userData?.email.toString()
        val createdAt = CurrentUser.userData?.createdAt.toString()
        val nameCheck = binding.tvEditNameCheck
        Log.d("data" ,"${name}, ${intro}")


        viewLifecycleOwner.lifecycleScope.launch {
            if (uri != null) {
                mainSharedViewModel.checkEdit(
                    uid = uid,
                    name = name,
                    email = email,
                    newProfile = uri.toString(),
                    beforeProfile = beforeProfile,
                    intro = intro,
                    createdAt = createdAt,
                    nameCheck = nameCheck
                )
            } else {
                mainSharedViewModel.checkEdit(
                    uid = uid,
                    name = name,
                    email = email,
                    newProfile = null,
                    beforeProfile = beforeProfile,
                    intro = intro,
                    createdAt = createdAt,
                    nameCheck = nameCheck
                )
            }
        }
    }


    private fun editTextCheck() {
        binding.etEditName.doAfterTextChanged {
            mainSharedViewModel.checkName(binding.etEditName.text.toString(), binding.tvEditNameCheck)
        }
    }
}