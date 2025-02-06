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
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMyProfileMakePostBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.mapper.toViewType
import com.example.project_sns.ui.util.dateFormat
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.profile.detail.PostRadiusImageAdapter
import com.example.project_sns.ui.model.ImageDataModel
import com.example.project_sns.ui.model.MapDataModel
import com.example.project_sns.ui.model.PostDataModel
import com.example.project_sns.ui.util.postDateFormat
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

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()



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
        collectFlow()
    }

    private fun initView() {

        binding.btnMakeConfirm.setText(R.string.post_edit_btn)
        binding.tvMakeTitle.setText(R.string.post_edit)


        mainSharedViewModel.postData.observe(viewLifecycleOwner) { data ->
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
                        data.mapData.placeUrl = null
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
            initData()
        }

        binding.ivMakePlusPhoto.setOnClickListener {
            getPhoto()
        }
    }


    private fun collectFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.postEditResult.collect {
                if (it == true) {
                    Toast.makeText(requireContext(), "게시물 수정 완료", Toast.LENGTH_SHORT).show()
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
                    if (imageToUri.pathSegments.contains("video") || imageToUri.toString().contains("video", ignoreCase = true)) {
                        imageList.add(ImageDataModel(imageToUri.toString(), imageToUri.toString(), "video"))
                    } else {
                        imageList.add(ImageDataModel(imageToUri.toString(), imageToUri.toString(),"image"))
                    }
                }
                initViewPager(imageList)
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

            binding.tvMakePhotoNumber.visibility = View.VISIBLE
            binding.tvMakePhotoSlash.visibility = View.VISIBLE
            binding.tvMakePhotoCurrentNumber.visibility = View.VISIBLE
            binding.tvMakePhotoNumber.text = imageList.size.toString()
            binding.vpMakeImageList.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val number = position + 1
                    binding.tvMakePhotoCurrentNumber.text = number.toString()
                }
            })
        } else {
            binding.vpMakeImageList.visibility = View.GONE
            binding.ivMakePlusPhoto.visibility = View.GONE

            binding.tvMakePhotoNumber.visibility = View.GONE
            binding.tvMakePhotoSlash.visibility = View.GONE
            binding.tvMakePhotoCurrentNumber.visibility = View.GONE
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
                getMapData?.placeUrl = mapData.getString("placeUrl")
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
            val postText = binding.etMakeText.text.toString()
            val time = postData?.createdAt.toString()
            val mapData = postData?.mapData
            val likeList = postData?.likePost ?: throw NullPointerException("NullLikeList")
            val editTime = postDateFormat()


            val data = PostDataModel(
                uid = uid,
                postId = postId,
                imageList = imageList,
                postText = postText,
                createdAt = time,
                editedAt = editTime,
                mapData = mapData,
                likePost = likeList
            )

            if (imageList.isEmpty()) {
                Toast.makeText(requireActivity(), "사진 선택 필요", Toast.LENGTH_SHORT).show()
            } else if (postText.isEmpty()) {
                Toast.makeText(requireActivity(), "글 작성 필요", Toast.LENGTH_SHORT).show()
            } else {
                mainSharedViewModel.editPost(data)
            }
        }
    }
}

