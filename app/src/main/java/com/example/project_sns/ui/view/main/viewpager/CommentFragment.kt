package com.example.project_sns.ui.view.main.viewpager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.project_sns.databinding.FragmentCommentBinding
import com.example.project_sns.ui.BaseBottomSheet
import com.example.project_sns.ui.view.main.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CommentFragment : BaseBottomSheet<FragmentCommentBinding>() {

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCommentBinding {
        return FragmentCommentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.resetCommentData()
        }
        initViewPager()
        setPage()

    }

    private fun initViewPager() {
        val viewPager = binding.vpComment
        viewPager.adapter = CommentViewPagerAdapter(this)
    }

    private fun setPage() {
        lifecycleScope.launch {
            mainSharedViewModel.startPage()
        }
        lifecycleScope.launch {
            mainSharedViewModel.currentPage.observe(viewLifecycleOwner) {
                binding.vpComment.setCurrentItem(it, true)
                Log.d("test_vp", "$it")
            }
        }
    }

}