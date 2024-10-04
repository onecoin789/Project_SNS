package com.example.project_sns.ui.view.main.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_sns.databinding.FragmentRequestPageBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.view.model.RequestModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RequestPageFragment : BaseFragment<FragmentRequestPageBinding>() {

    private val notificationViewModel: NotificationViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRequestPageBinding {
        return FragmentRequestPageBinding.inflate(inflater, container, false)
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

    private fun collectAcceptFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                notificationViewModel.acceptResult.collect { acceptResult ->
                    if (acceptResult == true) {
                        Toast.makeText(requireContext(), "성공", Toast.LENGTH_SHORT).show()
                    } else if (acceptResult == false) {
                        Toast.makeText(requireContext(), "실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun collectRejectFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                notificationViewModel.rejectResult.collect { rejectResult ->
                    if (rejectResult == true) {
                        Toast.makeText(requireContext(), "성공", Toast.LENGTH_SHORT).show()
                    } else if (rejectResult == false) {
                        Toast.makeText(requireContext(), "실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun initRv() {
        val followListAdapter =
            RequestListAdapter(object : RequestListAdapter.FollowItemClickListener {
                override fun onClickAcceptButton(item: RequestModel) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        notificationViewModel.acceptFriend(item.requestId, item.fromUid.uid, item.toUid)
                        collectAcceptFlow()
                    }
                }

                override fun onClickRejectButton(item: RequestModel) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        notificationViewModel.rejectRequest(item.requestId)
                        collectRejectFlow()
                    }
                }
            })

        with(binding.rvFollow) {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = followListAdapter
        }
        lifecycleScope.launch {
            notificationViewModel.requestList.observe(viewLifecycleOwner) { requestList ->
                followListAdapter.submitList(requestList)
            }
        }
    }
}