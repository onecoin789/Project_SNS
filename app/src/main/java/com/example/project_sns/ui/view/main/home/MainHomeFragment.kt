package com.example.project_sns.ui.view.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMainHomeBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainHomeFragment : BaseFragment<FragmentMainHomeBinding>() {

    private lateinit var auth: FirebaseAuth

    private val mainViewModel: MainViewModel by viewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private val lastVisibleItem = MutableStateFlow(0)

    private var isLoading: Boolean = false


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainHomeBinding {

        auth = FirebaseAuth.getInstance()

        return FragmentMainHomeBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigateView()
        initRv()


    }


    private fun initRv() {
        val postAdapter = HomePostAdapter { data ->
            viewLifecycleOwner.lifecycleScope.launch {
                mainSharedViewModel.getPostData(data.postData)
                mainSharedViewModel.postData.observe(viewLifecycleOwner) { postData ->
                    if (postData != null) {
                        mainSharedViewModel.getComment(postData.postId)

                    }
                }
            }

            findNavController().navigate(R.id.commentFragment)
        }
        val linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.rvHome) {
            layoutManager = linearLayoutManager
            adapter = postAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisible = linearLayoutManager.findLastVisibleItemPosition().plus(1)
                    if (!binding.rvHome.canScrollVertically(1)) {
                        binding.pbHome.setOnClickListener {
                            lastVisibleItem.value = lastVisible
                        }
                    } else if (lastVisibleItem.value == lastVisible) {
                        binding.pbHome.visibility = View.GONE
                    }
                }
            })
            viewLifecycleOwner.lifecycleScope.launch {
                mainViewModel.getAllPost()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.getPagingData(lastVisibleItem)
            delay(1000)
            mainViewModel.pagingData.collect { data ->
                postAdapter.submitList(data)
                Log.d("test_view", "$data")
            }
        }
    }

    private fun navigateView() {
        binding.clHomeLow.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_recommendFragment)
        }
        binding.clHomeMiddle.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_recommendFragment)
        }
        binding.clHomeHigh.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_recommendFragment)
        }
        binding.ivHomeDM.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_chatListFragment)
        }
    }

}