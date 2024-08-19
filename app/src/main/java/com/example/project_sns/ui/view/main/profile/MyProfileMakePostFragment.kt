package com.example.project_sns.ui.view.main.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMyProfileMakePostBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.util.dateFormat
import com.example.project_sns.ui.view.main.profile.detail.PostImageAdapter
import com.example.project_sns.ui.view.model.CommentDataModel
import com.example.project_sns.ui.view.model.PostDataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

@AndroidEntryPoint
class MyProfileMakePostFragment : BaseFragment<FragmentMyProfileMakePostBinding>() {

    private var uriList : ArrayList<String>? = arrayListOf()
    private val maxNumber = 10

    private var placeName: String? = null
    private var lat: String? = null
    private var lng: String? = null

    private val myProfileViewModel: MyProfileViewModel by viewModels()


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyProfileMakePostBinding {
        return FragmentMyProfileMakePostBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        getPhoto()
        getMapData()
    }

    private fun initView() {
        binding.ivMakeBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnMakeConfirm.setOnClickListener {
            collectFlow()
            initData()
        }

        binding.tvMakeLocation.setOnClickListener {
            findNavController().navigate(R.id.action_makePostFragment_to_myProfileSearchMapFragment)
        }
    }

    private fun initRv() {
        val imageAdapter = PostImageAdapter()
        imageAdapter.submitList(uriList)
        with(binding.rvMakeImageList) {
            adapter = imageAdapter
        }
        if (uriList != null) {
            binding.rvMakeImageList.visibility = View.VISIBLE
            binding.ivMakePlusPhoto.visibility = View.VISIBLE
            binding.ivMakePhoto.visibility = View.INVISIBLE
        } else {
            binding.rvMakeImageList.visibility = View.GONE
            binding.ivMakePhoto.visibility = View.GONE
            binding.ivMakePhoto.visibility = View.VISIBLE
        }
    }

    private fun getPhoto() {

        val registerForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        val clipData = result.data?.clipData
                        if (clipData != null) {
                            val clipDataSize = clipData.itemCount
                            val selectableCount = maxNumber - uriList!!.count()
                            if (clipDataSize > selectableCount) {
                                Toast.makeText(requireActivity(), "이미지는 최대 ${maxNumber}장까지 첨부할 수 있습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                for (i in 0 until clipDataSize) {
                                    uriList?.add(clipData.getItemAt(i).uri.toString())
                                }
                            }
                        } else {
                            val uri = result?.data?.data.toString()
                            uriList?.add(uri)
                        }
                        initRv()
                    }
                }
            }

        binding.clMakePhotoFrame.setOnClickListener {
            if (uriList?.count() == maxNumber) {
                Toast.makeText(requireActivity(), "이미지는 최대 ${maxNumber}장까지 첨부할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            registerForActivityResult.launch(intent)
        }

        binding.ivMakePlusPhoto.setOnClickListener {
            if (uriList?.count() == maxNumber) {
                Toast.makeText(
                    requireActivity(),
                    "이미지는 최대 ${maxNumber}장까지 첨부할 수 있습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            registerForActivityResult.launch(intent)
        }
    }

    private fun getMapData() {

        setFragmentResultListener("data") { data, bundle ->
            val mapData = bundle.getBundle("mapData")
            if (mapData != null) {
                binding.clMakeLocationText.visibility = View.VISIBLE
                binding.tvMakeLocationName.text = mapData.getString("placeName")
                binding.tvMakeLocationInfo.text = mapData.getString("addressName")

                placeName = mapData.getString("placeName")
                lat = mapData.getString("lat")
                lng = mapData.getString("lng")

            } else {
                binding.clMakeLocationText.visibility = View.GONE
            }

            binding.ivMakeMapDelete.setOnClickListener {
                mapData?.clear()
                binding.clMakeLocationText.visibility = View.GONE
            }
        }
    }

    private fun collectFlow() {

        viewLifecycleOwner.lifecycleScope.launch {
            myProfileViewModel.postUpLoadResult.collect {
                if (it == true) {
                    Toast.makeText(requireActivity(), "게시물을 생성 완료.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else if (it == false) {
                    Toast.makeText(requireActivity(), "게시물을 생성하지 못했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    private fun initData() {
        val auth = CurrentUser.userData
        val profileImage = auth?.profileImage
        val name = auth?.name.toString()
        val email = auth?.email.toString()
        val postText = binding.etMakeText.text.toString()
        val time = LocalDateTime.now()
        val data = PostDataModel(
            postId = UUID.randomUUID().toString(),
            profileImage = profileImage,
            name = name,
            email = email,
            image = uriList,
            postText = postText,
            lat = lat?.toDouble(),
            lng = lng?.toDouble(),
            placeName = placeName,
            createdAt = dateFormat(time),
            commentData = CommentDataModel("", "", "", "")
        )

        viewLifecycleOwner.lifecycleScope.launch {
            if (uriList != null) {
                myProfileViewModel.upLoadPost(data)
            } else {
                Toast.makeText(requireActivity(), "사진 선택 필요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}