package com.example.project_sns.ui.view.main.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.example.project_sns.databinding.FragmentMyProfileEditBinding
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.util.CheckEditProfile
import com.example.project_sns.ui.util.dateFormat
import com.example.project_sns.ui.util.refreshFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@AndroidEntryPoint
class MyProfileEditFragment : Fragment() {

    private var _binding: FragmentMyProfileEditBinding? = null
    private val binding get() = _binding!!

    private val myProfileViewModel: MyProfileViewModel by viewModels()

    private var uri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyProfileEditBinding.inflate(inflater, container, false)

        return binding.root
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
        getPhoto()
        editTextCheck()

        binding.btnEditConfirm.setOnClickListener {
            initData()
            collectFlow()
        }

        if (userData != null) {
            Glide.with(requireContext()).load(userData.profileImage).into(binding.ivEditPhoto)
            binding.etEditName.setText(userData.name)
            binding.etEditEmail.setText(userData.email)
            binding.etEditIntro.setText(userData.intro)
        }
    }

    private fun getPhoto() {

        val registerForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    Activity.RESULT_OK -> {
                        uri = result.data?.data!!
                        binding.ivEditPhoto.clipToOutline = true
                        Glide.with(requireContext()).load(uri).into(binding.ivEditPhoto)
                    }
                }
            }

        binding.clEditPhotoFrame.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            registerForActivityResult.launch(intent)
        }
    }

    private fun collectFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                myProfileViewModel.editEvent.collect { checkEdit ->
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
                myProfileViewModel.checkEdit(
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
                myProfileViewModel.checkEdit(
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
            myProfileViewModel.checkName(binding.etEditName.text.toString(), binding.tvEditNameCheck)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}