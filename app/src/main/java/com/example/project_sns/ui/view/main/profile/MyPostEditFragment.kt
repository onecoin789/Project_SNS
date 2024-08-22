package com.example.project_sns.ui.view.main.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMyProfileMakePostBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.util.dateFormat
import com.example.project_sns.ui.view.main.profile.detail.PostImageAdapter
import com.example.project_sns.ui.view.model.CommentDataModel
import com.example.project_sns.ui.view.model.MapDataModel
import com.example.project_sns.ui.view.model.PostDataModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPostEditFragment : BaseFragment<FragmentMyProfileMakePostBinding>() {

    private var uriList: List<String>? = listOf()
    private var beForeUriList: List<String>? = listOf()
    private var postData: PostDataModel? = null

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
                beForeUriList = data.image
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
            uriList = postData?.image
            uriList?.let { initViewPager(it) }
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
        TedImagePicker.with(requireContext()).max(10, "이미지는 최대 10장까지 첨부할 수 있습니다")
            .selectedUri(uriList?.map { it.toUri() }).startMultiImage { uri ->
                val getUriList = uri.map { it.toString() }
                initViewPager(getUriList)
                uriList = getUriList
            }
    }

    private fun initViewPager(image: List<String>) {
        val imageAdapter = PostImageAdapter()
        imageAdapter.submitList(image)
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
                image = uriList,
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

