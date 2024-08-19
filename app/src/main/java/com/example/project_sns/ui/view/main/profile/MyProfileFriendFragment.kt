package com.example.project_sns.ui.view.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.project_sns.databinding.FragmentMyProfileFriendBinding
import com.example.project_sns.ui.BaseFragment

class MyProfileFriendFragment : BaseFragment<FragmentMyProfileFriendBinding>() {


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyProfileFriendBinding {
        return FragmentMyProfileFriendBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.ivFriendBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}