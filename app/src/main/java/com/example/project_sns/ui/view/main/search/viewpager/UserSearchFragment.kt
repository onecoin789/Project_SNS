package com.example.project_sns.ui.view.main.search.viewpager

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LOG_TAG
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentUserSearchBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.model.PostModel
import com.example.project_sns.ui.model.UserDataModel
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.search.SearchUserListAdapter
import com.example.project_sns.ui.view.main.search.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserSearchFragment : BaseFragment<FragmentUserSearchBinding>() {

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private val searchViewModel: SearchViewModel by activityViewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserSearchBinding {
        return FragmentUserSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

    }


    private fun initView() {
        searchViewModel.query.observe(viewLifecycleOwner) { query ->
            if (query != null) {
                initRecyclerview()
            }
        }
    }

    private fun initRecyclerview() {
        val searchUserListAdapter = SearchUserListAdapter { item ->
            getDataByUid(item)
            findNavController().navigate(R.id.friendDetailFragment)
        }
        with(binding.rvSearchUser) {
            adapter = searchUserListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        searchViewModel.userSearchResult.observe(viewLifecycleOwner) { userSearchResult ->
            if (userSearchResult != null) {
                searchUserListAdapter.submitList(userSearchResult)
                Log.d("UserSearchFragment", "$userSearchResult")
            }
        }
    }

    private fun getDataByUid(item: UserDataModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.getUserData(item.uid)
            mainSharedViewModel.checkFriendRequest(item.uid)
        }
    }


}