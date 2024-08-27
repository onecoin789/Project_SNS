package com.example.project_sns.ui.view.main.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMyProfileMakePostBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.mapper.toViewType
import com.example.project_sns.ui.util.dateFormat
import com.example.project_sns.ui.view.main.profile.detail.PostImageAdapter
import com.example.project_sns.ui.view.model.CommentDataModel
import com.example.project_sns.ui.view.model.ImageDataModel
import com.example.project_sns.ui.view.model.MapDataModel
import com.example.project_sns.ui.view.model.PostDataModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

@AndroidEntryPoint
class MyProfileMakePostFragment : BaseFragment<FragmentMyProfileMakePostBinding>() {

    private var uriList: List<String>? = listOf()

    private var imageList: ArrayList<ImageDataModel> = arrayListOf()

    private var placeName: String? = null
    private var addressName: String? = null
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
        getMapData()
    }

    private fun initView() {
        binding.ivMakeBack.setOnClickListener {
            backButton()
        }

        binding.btnMakeConfirm.setOnClickListener {
            collectFlow()
            initData()
        }

        binding.tvMakeLocation.setOnClickListener {
            findNavController().navigate(R.id.action_makePostFragment_to_myProfileSearchMapFragment)
        }

        binding.clMakePhotoFrame.setOnClickListener {
            getPhoto()
        }

        binding.ivMakePlusPhoto.setOnClickListener {
            imageList.clear()
            getPhoto()
        }
    }

    private fun initViewPager() {
        val imageAdapter = PostImageAdapter()
        val listViewType = imageList.map { it.toViewType("video") }
        imageAdapter.submitList(listViewType)
        with(binding.vpMakeImageList) {
            adapter = imageAdapter
        }
        if (uriList != null) {
            binding.vpMakeImageList.visibility = View.VISIBLE
            binding.ivMakePlusPhoto.visibility = View.VISIBLE
            binding.ivMakePhoto.visibility = View.INVISIBLE
        } else {
            binding.vpMakeImageList.visibility = View.GONE
            binding.ivMakePhoto.visibility = View.GONE
        }
    }

    private fun getPhoto() {
        val selectedList = imageList.map { it.imageUri.toUri() }
        TedImagePicker.with(requireContext())
            .max(10, "이미지는 최대 10장까지 첨부할 수 있습니다")
            .imageAndVideo()
            .selectedUri(selectedList)
            .startMultiImage { uri ->
                val getUriList = uri.map { it.toString() }
                for (i in 0 until getUriList.count()) {
                    val imageToUri = getUriList[i].toUri()
                    if (imageToUri.pathSegments.contains("video")) {
                        imageList.add(ImageDataModel(imageToUri.toString(), "video"))
                    } else {
                        imageList.add(ImageDataModel(imageToUri.toString(), "image"))
                    }
                }
                initViewPager()
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
                addressName = mapData.getString("addressName")
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
        val uid = auth?.uid.toString()
        val profileImage = auth?.profileImage
        val name = auth?.name.toString()
        val email = auth?.email.toString()
        val postText = binding.etMakeText.text.toString()
        val time = LocalDateTime.now()


        val data = PostDataModel(
            uid = uid,
            postId = UUID.randomUUID().toString(),
            profileImage = profileImage,
            name = name,
            email = email,
            imageList = imageList,
            postText = postText,
            mapData = MapDataModel(placeName, addressName, lat?.toDouble(), lng?.toDouble()),
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