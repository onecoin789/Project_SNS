package com.example.project_sns.ui.view.main.search.viewpager

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentPostSearchBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.profile.PostViewModel
import com.example.project_sns.ui.view.main.search.SearchPostListAdapter
import com.example.project_sns.ui.view.main.search.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostSearchFragment : BaseFragment<FragmentPostSearchBinding>() {

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private val searchViewModel: SearchViewModel by activityViewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPostSearchBinding {
        return FragmentPostSearchBinding.inflate(inflater, container, false)
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
        val searchPostListAdapter = SearchPostListAdapter { item ->
            mainSharedViewModel.getPostData(item)
            findNavController().navigate(R.id.postDetailFragment)
        }
        with(binding.rvSearchPost) {
            adapter = searchPostListAdapter
            layoutManager = GridLayoutManager(requireActivity(), 3)
        }
        searchViewModel.postSearchResult.observe(viewLifecycleOwner) { postSearchResult ->
            if (postSearchResult != null) {
                searchPostListAdapter.submitList(postSearchResult)
                Log.d("PostSearchFragment", "$postSearchResult")
            }
        }
    }

}