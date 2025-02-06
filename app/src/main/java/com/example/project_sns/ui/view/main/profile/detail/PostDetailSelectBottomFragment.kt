package com.example.project_sns.ui.view.main.profile.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentPostDetailSelectBottomBinding
import com.example.project_sns.ui.BaseBottomSheet
import com.example.project_sns.ui.BaseSnackBar
import com.example.project_sns.ui.model.MyPostListModel
import com.example.project_sns.ui.model.PostModel
import com.example.project_sns.ui.room.MyPostListDAO
import com.example.project_sns.ui.room.MyPostListDatabase
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.profile.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class PostDetailSelectBottomFragment : BaseBottomSheet<FragmentPostDetailSelectBottomBinding>() {

    private val postViewModel: PostViewModel by viewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPostDetailSelectBottomBinding {

        return FragmentPostDetailSelectBottomBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        lifecycleScope.launch {
            mainSharedViewModel.postData.observe(viewLifecycleOwner) { postData ->
                if (postData != null) {
                    postViewModel.getPostDataByPostId(postData.postId)

                    val mapData = postData.mapData
                    Log.d("Tag_Location", "$mapData")
                    if (mapData != null) {
                        val placeName = mapData.placeName
                        if (placeName != null) {
                            binding.clMoreLocation.visibility = View.VISIBLE
                        } else {
                            binding.clMoreLocation.visibility = View.GONE
                        }
                    }
                }
            }
        }


        binding.clMoreDelete.setOnClickListener {
            inflateDialog("정말 삭제하시겠어요?", "삭제하시면 복구하실 수 없습니다.")
        }

        binding.clMoreEdit.setOnClickListener {
            findNavController().navigate(R.id.action_postDetailSelectBottomFragment_to_myPostEditFragment)
        }

        binding.clMoreLocation.setOnClickListener {
            findNavController().navigate(R.id.action_postDetailSelectBottomFragment_to_postDetailMapFragment)
        }
    }

    private fun initRoom(data: PostModel) {
        val myPostListDAO =
            MyPostListDatabase.getMyPostListDatabase(requireContext()).getMyPostListDAO()
        val id = UUID.randomUUID().toString()
        val user = data.userData
        val post = data.postData
        val myPostListData = MyPostListModel(
            id,
            user.name,
            user.email,
            user.profileImage,
            user.intro,
            post.imageList!![0].downloadUrl,
            post.postText,
            post.createdAt
        )

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                myPostListDAO.insertData(myPostListData)
            }.onSuccess {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "good", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }


    }
}