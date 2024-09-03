package com.example.project_sns.ui.view.main.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMyProfileMakePostBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.mapper.toViewType
import com.example.project_sns.ui.util.dateFormat
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.profile.detail.PostRadiusImageAdapter
import com.example.project_sns.ui.view.model.ImageDataModel
import com.example.project_sns.ui.view.model.MapDataModel
import com.example.project_sns.ui.view.model.PostDataModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@AndroidEntryPoint
class MyPostEditFragment : BaseFragment<FragmentMyProfileMakePostBinding>() {

    private var postData: PostDataModel? = null

    private var beforeImageList: ArrayList<ImageDataModel> = arrayListOf()

    private var imageList: ArrayList<ImageDataModel> = arrayListOf()

    private var getMapData: MapDataModel? = null

    private val myProfileViewModel: MainSharedViewModel by activityViewModels()



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

        binding.btnMakeConfirm.setText(R.string.post_edit_btn)
        binding.tvMakeTitle.setText(R.string.post_edit)


        myProfileViewModel.postData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                if (data.postText != null) {
                    binding.etMakeText.setText(data.postText)
                }

                if (data.mapData?.placeName != null) {
                    binding.clMakeLocationText.visibility = View.VISIBLE
                    binding.tvMakeLocationName.text = data.mapData.placeName
                    binding.tvMakeLocationInfo.text = data.mapData.addressName

                    binding.ivMakeMapDelete.setOnClickListener {
                        data.mapData.placeName = null
                        data.mapData.addressName = null
                        data.mapData.lat = null
                        data.mapData.lng = null
                        binding.clMakeLocationText.visibility = View.GONE
                    }
                } else {
                    binding.clMakeLocationText.visibility = View.GONE
                }
            }
            postData = data
            imageList = data?.imageList as ArrayList<ImageDataModel>
            getMapData = postData?.mapData

            initViewPager(imageList)
        }

        binding.tvMakeLocation.setOnClickListener {
            findNavController().navigate(R.id.myProfileSearchMapFragment)
        }

        binding.ivMakeBack.setOnClickListener {
            backButton()
        }

        binding.btnMakeConfirm.setOnClickListener {
            collectFlow()
            initData()
        }

        binding.ivMakePlusPhoto.setOnClickListener {
            getPhoto()
        }
    }


    private fun collectFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            myProfileViewModel.postEditResult.collect {
                if (it == true) {
                    Toast.makeText(requireContext(), "게시물 수정 성공", Toast.LENGTH_SHORT).show()
                    backToMain()
                } else if (it == false) {
                    Toast.makeText(requireContext(), "게시물 수정 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPhoto() {
        val selectImage = imageList.map { it.imageUri.toUri() }
        TedImagePicker.with(requireContext())
            .max(10, "이미지는 최대 10장까지 첨부할 수 있습니다")
            .imageAndVideo()
            .selectedUri(selectImage)
            .startMultiImage { uri ->
                imageList.clear()
                for (i in 0 until uri.count()) {
                    val imageToUri = uri[i]
                    Log.d("fuck", "${imageToUri}")
                    if (imageToUri.pathSegments.contains("video") || imageToUri.toString().contains("video", ignoreCase = true)) {
                        imageList.add(ImageDataModel(imageToUri.toString(), imageToUri.toString(), "video"))
                    } else {
                        imageList.add(ImageDataModel(imageToUri.toString(), imageToUri.toString(),"image"))
                    }
                }
                initViewPager(imageList)
                Log.d("image_data", "$imageList")
                Log.d("image_data_before2", "")
            }
    }

    private fun initViewPager(imageList: List<ImageDataModel>?) {
        val imageAdapter = PostRadiusImageAdapter()
        val listViewType = imageList?.map { it.toViewType("video") }
        imageAdapter.submitList(listViewType)
        binding.vpMakeImageList.adapter = imageAdapter

        if (imageList != null) {
            binding.vpMakeImageList.visibility = View.VISIBLE
            binding.ivMakePlusPhoto.visibility = View.VISIBLE
            binding.ivMakePhoto.visibility = View.INVISIBLE
        } else {
            binding.vpMakeImageList.visibility = View.GONE
            binding.ivMakePlusPhoto.visibility = View.GONE
        }
    }

    private fun getMapData() {

        setFragmentResultListener("data") { data, bundle ->
            val mapData = bundle.getBundle("mapData")
            if (mapData != null) {
                binding.clMakeLocationText.visibility = View.VISIBLE
                binding.tvMakeLocationName.text = mapData.getString("placeName")
                binding.tvMakeLocationInfo.text = mapData.getString("addressName")

                getMapData?.placeName = mapData.getString("placeName")
                getMapData?.addressName = mapData.getString("addressName")
                getMapData?.lat = mapData.getString("lat")?.toDouble()
                getMapData?.lng = mapData.getString("lng")?.toDouble()

            } else {
                binding.clMakeLocationText.visibility = View.GONE
            }

            binding.ivMakeMapDelete.setOnClickListener {
                mapData?.clear()
                binding.clMakeLocationText.visibility = View.GONE
            }
        }
    }

    private fun initData() {
        if (postData != null) {
            val uid = postData?.uid.toString()
            val postId = postData?.postId.toString()
            val profileImage = postData?.profileImage
            val name = postData?.name.toString()
            val email = postData?.email.toString()
            val postText = binding.etMakeText.text.toString()
            val time = postData?.createdAt.toString()
            val mapData = postData?.mapData
            val editTime = dateFormat(LocalDateTime.now())


            val data = PostDataModel(
                uid = uid,
                postId = postId,
                profileImage = profileImage,
                name = name,
                email = email,
                imageList = imageList,
                postText = postText,
                createdAt = time,
                editedAt = editTime,
                mapData = mapData
            )

            viewLifecycleOwner.lifecycleScope.launch {
                myProfileViewModel.editPost(data)
            }
        }
    }
}

