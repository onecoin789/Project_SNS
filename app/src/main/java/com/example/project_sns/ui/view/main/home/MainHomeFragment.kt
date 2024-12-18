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
import com.example.project_sns.FcmUtil
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMainHomeBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.model.PostModel
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.MainViewModel
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

        refreshRecyclerView()
        navigateView()
        initRv()
        getPostData()
        refreshLayout()
        getAccessToken()

    }

    private fun getAccessToken() {
        CoroutineScope(Dispatchers.IO).launch {
            if (FcmUtil.accessToken == null) {
                val asset = resources.assets.open("service-account.json")
                val googleCredential = GoogleCredentials.fromStream(asset)
                    .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
                googleCredential.refresh()
                val accessToken = googleCredential.accessToken.tokenValue
                Log.d("accessToken", accessToken)
                FcmUtil.accessToken = accessToken
            }
        }
    }

    private fun getPostData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val lastVisible = mainViewModel.postLastVisibleItem
            mainViewModel.getPagingData(lastVisible)
        }
    }

    private fun refreshLayout() {
        binding.refreshLayoutHome.setOnRefreshListener {
            CoroutineScope(Dispatchers.Main).launch {
                binding.clHomeItemMore.visibility = View.GONE
                binding.rvHome.visibility = View.GONE
                refreshRecyclerView()
                delay(500)
                getPostData()
                mainViewModel.postLastVisibleItem(0)
                initRv()
                delay(1000)
                binding.rvHome.visibility = View.VISIBLE
                binding.clHomeItemMore.visibility = View.VISIBLE
                binding.refreshLayoutHome.isRefreshing = false
            }
            // FIXME: 새로고침 후 more item 안됨
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
                getDataByUid(item)
                findNavController().navigate(R.id.action_mainFragment_to_friendDetailFragment)
            }

            override fun onClickProfileNameItem(item: PostModel) {
                getDataByUid(item)
                findNavController().navigate(R.id.action_mainFragment_to_friendDetailFragment)
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
                                // FIXME: 하단에 위치하면 계속해서 more item 하는 버그있음 수정필요
                            }
                        }
                    }
                }
            })
        }

        mainViewModel.pagingData.observe(viewLifecycleOwner) { data ->
            val dataByCreatedAt = data.sortedByDescending { it.postData.createdAt }

            postList = dataByCreatedAt.toMutableList()
            postAdapter.submitList(postList)
            postAdapter.notifyItemInserted(data.size - 1)

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
                mainViewModel.postLastVisibleItem(lastVisible)
//                mainViewModel.postLastVisibleItem.value = lastVisible
            }
            delay(500)
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
            getFriendList()
            findNavController().navigate(R.id.action_mainFragment_to_chatListFragment)
        }
        binding.ivHomeNotification.setOnClickListener {
            findNavController().navigate(R.id.notificationFragment)
//            refreshRecyclerView()
        }
    }

    private fun refreshRecyclerView() {
        mainViewModel.postLastVisibleItem(0)
        mainViewModel.resetPagingData()
    }

    private fun getDataByUid(item: PostModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.getUserData(item.userData.uid)
            mainSharedViewModel.getUserPost(item.userData.uid)
            mainSharedViewModel.checkFriendRequest(item.userData.uid)
        }
    }

    private fun getFriendList() {
        val currentUser = CurrentUser.userData?.uid
        viewLifecycleOwner.lifecycleScope.launch {
            if (currentUser != null) {
                mainSharedViewModel.getFriendList(currentUser)
            }
        }
    }

}