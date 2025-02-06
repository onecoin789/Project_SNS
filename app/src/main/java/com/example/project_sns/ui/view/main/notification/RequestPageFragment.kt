package com.example.project_sns.ui.view.main.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_sns.databinding.FragmentRequestPageBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.model.RequestDataModel
import com.example.project_sns.ui.model.RequestModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RequestPageFragment : BaseFragment<FragmentRequestPageBinding>() {

    private val notificationViewModel: NotificationViewModel by viewModels()

    private lateinit var requestListAdapter: RequestListAdapter

    private var requestList: MutableList<RequestModel?> = mutableListOf()

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
        checkRequestList()


    }

    private fun getRequestList() {
        viewLifecycleOwner.lifecycleScope.launch {
            notificationViewModel.getRequestList(notificationViewModel.requestLastVisibleItem)
        }
    }

    private fun checkRequestList() {
        notificationViewModel.checkRequestList()
        notificationViewModel.checkRequestResult.observe(viewLifecycleOwner) { result ->
            if (result == true) {
                getRequestList()
            } else {
                getRequestList()
            }
        }
    }

    private fun initView() {
        binding.ivRequestBack.setOnClickListener {
            backButton()
        }
    }

    private fun collectAcceptFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                notificationViewModel.acceptResult.collect { acceptResult ->
                    if (acceptResult == true) {
                        getRefreshItem()
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
                        getRefreshItem()
                    }
                }
            }
        }
    }

    private fun initRv() {
        requestListAdapter =
            RequestListAdapter(object : RequestListAdapter.FollowItemClickListener {
                override fun onClickAcceptButton(item: RequestModel) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        notificationViewModel.acceptFriend(
                            item.requestId,
                            item.fromUid.uid,
                            item.toUid
                        )
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

        val linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.rvRequest) {
            layoutManager = linearLayoutManager
            adapter = requestListAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val lastVisible = linearLayoutManager.findLastVisibleItemPosition().plus(1)
                    if (!binding.rvRequest.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        moreItem(lastVisible)
                        Log.d(
                            "request_visible",
                            "${notificationViewModel.requestLastVisibleItem.value}, $lastVisible"
                        )
                    }
                }
            })
        }

        notificationViewModel.requestList.observe(viewLifecycleOwner) { data ->
            requestList = data.toMutableList()
            requestListAdapter.submitList(requestList)


            if (data.isNotEmpty()) {
                binding.rvRequest.visibility = View.VISIBLE
                binding.tvRequestIntro.visibility = View.GONE
            } else {
                binding.rvRequest.visibility = View.GONE
                binding.tvRequestIntro.visibility = View.VISIBLE
            }
        }
    }

    private fun moreItem(lastVisible: Int) {
        val mRecyclerView = binding.rvRequest
        val runnable = kotlinx.coroutines.Runnable {
            requestList.add(null)
            requestListAdapter.notifyItemInserted(requestList.size - 1)
        }
        mRecyclerView.post(runnable)

        CoroutineScope(Dispatchers.Main).launch {
            val runnableMore = kotlinx.coroutines.Runnable {
                requestList.removeAt(requestList.size - 1)
                requestListAdapter.notifyItemRemoved(requestList.size)
                notificationViewModel.requestLastVisibleItem.value = lastVisible
            }
            delay(1000)
            runnableMore.run()
        }
    }

    private fun getRefreshItem() {
        CoroutineScope(Dispatchers.Main).launch {
            binding.rvRequest.visibility = View.GONE
            binding.pbRequest.visibility = View.VISIBLE
            notificationViewModel.clearRequestItem()
            binding.tvRequestIntro.visibility = View.GONE
            delay(100)
            getRequestList()
            delay(100)
            binding.rvRequest.visibility = View.VISIBLE
            binding.pbRequest.visibility = View.GONE
        }
    }
}