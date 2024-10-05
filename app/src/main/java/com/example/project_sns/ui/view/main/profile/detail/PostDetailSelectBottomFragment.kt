package com.example.project_sns.ui.view.main.profile.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentPostDetailSelectBottomBinding
import com.example.project_sns.ui.BaseBottomSheet
import com.example.project_sns.ui.view.main.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailSelectBottomFragment : BaseBottomSheet<FragmentPostDetailSelectBottomBinding>() {

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
                val mapData = postData?.mapData
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
}