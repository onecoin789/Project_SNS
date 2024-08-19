package com.example.project_sns.ui.view.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentFriendDetailBinding
import com.example.project_sns.ui.BaseFragment

class FriendDetailFragment : BaseFragment<FragmentFriendDetailBinding>() {


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFriendDetailBinding {
        return FragmentFriendDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}