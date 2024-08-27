package com.example.project_sns.ui.view.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMyProfileMakePostBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.mapper.toViewType
import com.example.project_sns.ui.view.main.profile.detail.PostImageAdapter
import com.example.project_sns.ui.view.model.ImageDataModel
import com.example.project_sns.ui.view.model.PostDataModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPostEditFragment : BaseFragment<FragmentMyProfileMakePostBinding>() {

    private var uriList: List<String>? = listOf()
    private var beForeUriList: List<String>? = listOf()
    private var postData: PostDataModel? = null

    private var imageList: ArrayList<ImageDataModel> = arrayListOf()

    private val myProfileViewModel: MyProfileViewModel by activityViewModels()


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyProfileMakePostBinding {

        return FragmentMyProfileMakePostBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

        binding.btnMakeConfirm.setText(R.string.post_edit_btn)
        binding.tvMakeTitle.setText(R.string.post_edit)


        myProfileViewModel.postData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                beForeUriList = data.imageList?.map { it.imageUri }
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
            initViewPager(imageList)
        }

        binding.ivMakeBack.setOnClickListener {
            backButton()
        }

        binding.btnMakeConfirm.setOnClickListener {
            collectFlow()
            initData()
        }

        binding.ivMakePlusPhoto.setOnClickListener {
            imageList.clear()
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
                val getUriList = uri.map { it.toString() }
                for (i in 0 until getUriList.count()) {
                    val imageToUri = getUriList[i].toUri()
                    if (imageToUri.pathSegments.contains("video")) {
                        imageList.add(ImageDataModel(imageToUri.toString(), "video"))
                    } else {
                        imageList.add(ImageDataModel(imageToUri.toString(), "image"))
                    }
                }
                initViewPager(imageList)
            }
    }

    private fun initViewPager(imageList: List<ImageDataModel>?) {
        val imageAdapter = PostImageAdapter()
        val listViewType = imageList?.map { it.toViewType("video") }
        imageAdapter.submitList(listViewType)
        binding.vpMakeImageList.adapter = imageAdapter

        if (uriList != null) {
            binding.vpMakeImageList.visibility = View.VISIBLE
            binding.ivMakePlusPhoto.visibility = View.VISIBLE
            binding.ivMakePhoto.visibility = View.INVISIBLE
        } else {
            binding.vpMakeImageList.visibility = View.GONE
            binding.ivMakePlusPhoto.visibility = View.GONE
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
            val commentData = postData?.commentData

            val data = PostDataModel(
                uid = uid,
                postId = postId,
                profileImage = profileImage,
                name = name,
                email = email,
                imageList = imageList,
                postText = postText,
                createdAt = time,
                mapData = mapData,
                commentData = commentData
            )

            viewLifecycleOwner.lifecycleScope.launch {
                myProfileViewModel.editPost(data)
            }
        }
    }
}

