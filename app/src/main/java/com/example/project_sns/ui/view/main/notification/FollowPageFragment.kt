package com.example.project_sns.ui.view.main.notification

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentFollowPageBinding
import com.example.project_sns.domain.model.RequestEntity
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.model.RequestDataModel
import com.example.project_sns.ui.view.model.RequestModel
import com.example.project_sns.ui.view.model.UserDataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FollowPageFragment : BaseFragment<FragmentFollowPageBinding>() {

    private val notificationViewModel: NotificationViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFollowPageBinding {
        return FragmentFollowPageBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getRequestList()
        initView()
        initRv()

    }

    private fun getRequestList() {
        viewLifecycleOwner.lifecycleScope.launch {
            notificationViewModel.getRequestList()
        }
    }

    private fun initView() {
        binding.ivFollowBack.setOnClickListener {
            backButton()
        }
    }

    private fun initRv() {
        val followListAdapter = FollowListAdapter(object : FollowListAdapter.FollowItemClickListener {
            override fun onClickAcceptButton(item: RequestModel) {
                TODO("Not yet implemented")
            }

            override fun onClickRejectButton(item: RequestModel) {
                TODO("Not yet implemented")
            }
        })

        with(binding.rvFollow) {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = followListAdapter
        }
        lifecycleScope.launch {
            notificationViewModel.requestList.observe(viewLifecycleOwner) { requestList ->
                Log.d("requestList", "$requestList")
                followListAdapter.submitList(requestList)
            }
        }
    }
}