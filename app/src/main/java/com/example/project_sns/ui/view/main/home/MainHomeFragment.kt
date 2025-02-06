package com.example.project_sns.ui.view.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
import com.example.project_sns.ui.util.notTouch
import com.example.project_sns.ui.util.sharePost
import com.example.project_sns.ui.util.touch
import com.example.project_sns.ui.view.chat.ChatSharedViewModel
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.MainViewModel
import com.example.project_sns.ui.view.main.profile.PostViewModel
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainHomeFragment : BaseFragment<FragmentMainHomeBinding>() {

    companion object {
        private const val TAG = "MainHomeFragment"
    }

    private lateinit var auth: FirebaseAuth

    private val mainViewModel: MainViewModel by viewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private val chatSharedViewModel: ChatSharedViewModel by activityViewModels()

    private val postViewModel: PostViewModel by viewModels()

    private var postList: MutableList<PostModel?> = mutableListOf()

    private lateinit var linearLayoutManager: LinearLayoutManager

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

        navigateView()
        initRv()
        getPostData()
        getAccessToken()
        refreshLayout()
        refreshRecyclerView()
        initPostData()
        scrollListener()

        navigateFragment()


        mainSharedViewModel.loginSessionResult.observe(viewLifecycleOwner) { result ->
            if (result == false) {
                binding.clHomeToolbar.visibility = View.GONE
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
                notTouch(activity)
                refreshRecyclerView()
                binding.rvHome.visibility = View.GONE
                delay(500)
                initRv()
                getPostData()
                delay(1000)
                touch(activity)
                binding.rvHome.visibility = View.VISIBLE
                binding.refreshLayoutHome.isRefreshing = false
            }
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

            override fun onClickMapItem(item: PostModel) {
                mainSharedViewModel.getPostData(item.postData)
                findNavController().navigate(R.id.postDetailMapFragment)
            }

            override fun onClickLikeButtonItem(item: PostModel) {
                postViewModel.updateLike(item.postData.postId, true)
            }

            override fun onClickLikeCancelButtonItem(item: PostModel) {
                postViewModel.updateLike(item.postData.postId, false)
            }

            override fun onClickShareButtonItem(item: PostModel) {
                startActivity(sharePost(item.postData.postText.toString()))
            }

        })
        linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.rvHome)
        {
            layoutManager = linearLayoutManager
            adapter = postAdapter
        }
    }

    private fun scrollListener() {
        binding.rvHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val lastVisible = linearLayoutManager.findLastVisibleItemPosition().plus(1)
                super.onScrollStateChanged(recyclerView, newState)
                if (!binding.rvHome.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    moreItem(lastVisible)
                    Log.d("Main_lastVisible", "$lastVisible")
                }
            }
        })
    }

    private fun initPostData() {
        mainViewModel.pagingData.observe(viewLifecycleOwner) { data ->
            val dataByCreatedAt = data.sortedByDescending { it.postData.createdAt }
            postList = dataByCreatedAt.toMutableList()
            postAdapter.submitList(postList)
            postAdapter.notifyItemInserted(data.size - 1)
        }
    }

    private fun moreItem(lastVisible: Int) {
        val mRecyclerView = binding.rvHome
//        val runnable = kotlinx.coroutines.Runnable {
//            notTouch(activity)
//            postList.add(null)
//            postAdapter.notifyItemInserted(postList.size - 1)
//        }

//        mRecyclerView.post(runnable)
        CoroutineScope(Dispatchers.Main).launch {
            val runnableMore = kotlinx.coroutines.Runnable {
//                postList.removeAt(postList.size - 1)
//                postAdapter.notifyItemRemoved(postList.size)
                mainViewModel.postLastVisibleItem(lastVisible)
            }
            delay(300)
            runnableMore.run()
            delay(300)
            touch(activity)
        }
    }


    private fun navigateView() {
        binding.ivHomeDM.setOnClickListener {
            getFriendList()
            findNavController().navigate(R.id.action_mainFragment_to_chatListFragment)
        }
        binding.ivHomeNotification.setOnClickListener {
            findNavController().navigate(R.id.notificationFragment)
        }
    }

    private fun refreshRecyclerView() {
        mainViewModel.postLastVisibleItem(0)
        mainViewModel.resetPagingData()
        postList.clear()
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


    private fun navigateFragment() {
        val getChatRoomId = requireActivity().intent.getStringExtra("chatRoomId")
        val getUid = requireActivity().intent.getStringExtra("uid")

        Log.d(TAG, "$getChatRoomId, $getUid")

        if (FcmUtil.clickState == true) {
            if (getChatRoomId != null && getUid != null) {
                chatSharedViewModel.getChatRoomId(getChatRoomId)
                chatSharedViewModel.getChatRoomData(getUid)
                chatSharedViewModel.checkChatRoomExist(getUid)
                mainSharedViewModel.getUserData(getUid)
            }
            findNavController().navigate(R.id.chatRoomFragment)
        }
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

}