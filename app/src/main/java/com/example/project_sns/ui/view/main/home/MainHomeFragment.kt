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
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.MainViewModel
import com.example.project_sns.ui.view.main.comment.CommentAdapter
import com.example.project_sns.ui.view.model.CommentModel
import com.example.project_sns.ui.view.model.PostModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainHomeFragment : BaseFragment<FragmentMainHomeBinding>() {

    private lateinit var auth: FirebaseAuth

    private val mainViewModel: MainViewModel by viewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var postList: MutableList<PostModel?> = mutableListOf()

    private lateinit var postAdapter: HomePostAdapter


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainHomeBinding {

        auth = FirebaseAuth.getInstance()

        return FragmentMainHomeBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainSharedViewModel.pagingData.observe(viewLifecycleOwner) {
            Log.d("Tag2", "${it.size}")
        }
        lifecycleScope.launch {
            mainViewModel.allPostData.collect {
                Log.d("Tag1", "${it.size}")
            }
        }

        navigateView()
        initRv()
        getPostData()

    }

    private fun getPostData() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.getPagingData()
        }
    }


    private fun initRv() {
        postAdapter = HomePostAdapter(object : HomePostAdapter.PostItemClickListener {
            override fun onClickCommentItem(item: PostModel) {
                viewLifecycleOwner.lifecycleScope.launch {
                    mainSharedViewModel.getPostData(item.postData)
                }
                findNavController().navigate(R.id.commentFragment)
            }

            override fun onClickProfileImageItem(item: PostModel) {
                viewLifecycleOwner.lifecycleScope.launch {
                    mainSharedViewModel.getUserData(item.userData.uid)
                    mainSharedViewModel.getUserPost(item.userData.uid)
                }
                findNavController().navigate(R.id.action_mainFragment_to_friendDetailFragment)
                refreshRecyclerView()
            }

            override fun onClickProfileNameItem(item: PostModel) {
                viewLifecycleOwner.lifecycleScope.launch {
                    mainSharedViewModel.getUserData(item.userData.uid)
                    mainSharedViewModel.getUserPost(item.userData.uid)
                }
                findNavController().navigate(R.id.action_mainFragment_to_friendDetailFragment)
                refreshRecyclerView()
            }

        })

        val linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.rvHome) {
            layoutManager = linearLayoutManager
            adapter = postAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisible = linearLayoutManager.findLastVisibleItemPosition().plus(1)
                    if (!binding.rvHome.canScrollVertically(1)) {
                        binding.clHomeItemMore.setOnClickListener {
                            lifecycleScope.launch {
                                moreItem(lastVisible)
                            }
                        }
                    }
                }
            })
        }

        mainSharedViewModel.pagingData.observe(viewLifecycleOwner) { data ->
            val dataByCreatedAt = data.sortedByDescending { it.postData.createdAt }

            postList = dataByCreatedAt.toMutableList()
            postAdapter.submitList(postList)
            postAdapter.notifyItemInserted(data.size - 1)

            if (data.isNotEmpty() && data != null) {
                binding.clHomeItemMore.visibility = View.VISIBLE
            } else {
                binding.clHomeItemMore.visibility = View.GONE
            }
            Log.d("test_view", "$data")
        }
    }

    private fun moreItem(lastVisible: Int) {
        val mRecyclerView = binding.rvHome
        val runnable = kotlinx.coroutines.Runnable {
            postList.add(null)
            postAdapter.notifyItemInserted(postList.size - 1)
        }
        mRecyclerView.post(runnable)

        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            val runnableMore = kotlinx.coroutines.Runnable {
                postList.removeAt(postList.size - 1)
                postAdapter.notifyItemRemoved(postList.size)
                mainSharedViewModel.postLastVisibleItem(lastVisible)
//                mainViewModel.postLastVisibleItem.value = lastVisible
            }
            delay(1000)
            runnableMore.run()
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
        binding.ivHomeNotification.setOnClickListener {
            findNavController().navigate(R.id.notificationFragment)
//            refreshRecyclerView()
        }
    }

    private fun refreshRecyclerView() {
        mainSharedViewModel.postLastVisibleItem(0)
        mainSharedViewModel.resetPagingData()
    }

}