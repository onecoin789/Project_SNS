package com.example.project_sns.ui.view.chat.chatroom

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.project_sns.databinding.FragmentImageViewerBinding
import com.example.project_sns.ui.mapper.toViewType
import com.example.project_sns.ui.model.ImageDataModel
import com.example.project_sns.ui.view.main.profile.detail.PostRadiusImageAdapter

class ChatRoomImageViewerDialog(private val imageList: List<ImageDataModel>): DialogFragment() {

    private var _binding: FragmentImageViewerBinding? = null
    val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageViewerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewPager()

    }

    private fun initViewPager() {
        val imageAdapter = PostRadiusImageAdapter()
        val listViewType = imageList.map { it.toViewType("video") }
        imageAdapter.submitList(listViewType)
        binding.vpImageViewer.adapter = imageAdapter
    }

    private fun initView() {
        binding.ivImageViewerClose.setOnClickListener {
            dismiss()
        }
        binding.vpImageViewer.registerOnPageChangeCallback(object : OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val number = position + 1
                binding.tvImageViewerCurrentPage.text = number.toString()
            }
//            override fun onPageScrolled(
//                position: Int,
//                positionOffset: Float,
//                positionOffsetPixels: Int
//            ) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
//                binding.tvImageViewerCurrentPage.text = (position + 1).toString()
//            }
        })
        binding.tvImageViewerListSize.text = imageList.size.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}