package com.example.project_sns.ui.view.main.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMainSearchBinding
import com.example.project_sns.domain.SearchViewType
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.BaseSnackBar
import com.example.project_sns.ui.util.deleteTextWatcher
import com.example.project_sns.ui.util.textWatcher
import com.example.project_sns.ui.view.main.search.viewpager.SearchViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainSearchFragment : BaseFragment<FragmentMainSearchBinding>() {

    private val searchViewModel: SearchViewModel by activityViewModels()

    private var searchViewType: SearchViewType = SearchViewType.USER_SEARCH

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainSearchBinding {
        return FragmentMainSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextWatcher()
        initViewPager()
        initBottomBar()
        initView()
        textWatcher()


    }


    private fun initView() {
        searchViewModel.userSearchResult.observe(viewLifecycleOwner) { userSearchResult ->
            if (userSearchResult != null) {
                binding.tvSearchNone.visibility = View.GONE
                binding.vpSearchTab.visibility = View.VISIBLE
                Log.d("MainSearchFragment", "$userSearchResult")
            } else {
                binding.tvSearchNone.visibility = View.VISIBLE
                binding.vpSearchTab.visibility = View.GONE
                Log.d("MainSearchFragment", "null")
            }
        }
    }


    private fun editTextWatcher() {

        val editText = binding.etSearch
        val deleteButton = binding.ivSearchDelete

//        binding.ivSearch.setOnClickListener {
//            if (editText.text.isNotEmpty()) {
//                val keyword = binding.etSearch.text.toString()
//                searchViewModel.getSearchResult(keyword)
//                searchViewModel.getKeyword(keyword)
//            } else {
//                Toast.makeText(requireContext(), "검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
//            }
//        }

        binding.ivSearchDelete.setOnClickListener {
            editText.text.clear()
        }

        deleteTextWatcher(editText, deleteButton)

    }

    private fun initViewPager() {
        val viewPager = binding.vpSearchTab
        val tabLayout = binding.tlSearchCategory
        viewPager.adapter = SearchViewPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.setText("유저 검색")
                1 -> tab.setText("게시물 검색")
            }
        }.attach()

        binding.tlSearchCategory.tabTextColors =
            ContextCompat.getColorStateList(requireContext(), R.color.tab_search_text_selector)
    }

    private fun initBottomBar() {
        with(binding) {
            vpSearchTab.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> {
                            tvSearchNone.text = "유저를 검색해보세요!"
                            binding.etSearch.text.clear()
                            searchViewModel.clearSearchData()
                            searchViewType = SearchViewType.USER_SEARCH
                        }

                        1 -> {
                            tvSearchNone.text = "게시물을 검색해보세요!"
                            binding.etSearch.text.clear()
                            searchViewModel.clearSearchData()
                            searchViewType = SearchViewType.POST_SEARCH
                        }
                    }
                }
            })
        }
    }

    private fun textWatcher() {

        val editText = binding.etSearch

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                getSearch(editText)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                getSearch(editText)
            }

            override fun afterTextChanged(s: Editable?) {
                getSearch(editText)
            }
        })
    }

    private fun getSearch(editText: EditText) {

        val viewPager = binding.vpSearchTab
        val text = binding.tvSearchNone

        when(searchViewType) {
            SearchViewType.USER_SEARCH -> {
                if (editText.text.isEmpty()) {
                    viewPager.visibility = View.GONE
                    text.visibility = View.VISIBLE
                    searchViewModel.clearUserSearchResult()
                } else {
                    val query = binding.etSearch.text.toString()
                    viewPager.visibility = View.VISIBLE
                    text.visibility = View.GONE
                    searchViewModel.getQuery(query)
                    searchViewModel.getUserSearchResult(query)
                }
            }
            SearchViewType.POST_SEARCH -> {
                if (editText.text.isEmpty()) {
                    viewPager.visibility = View.GONE
                    text.visibility = View.VISIBLE
                    searchViewModel.clearPostSearchResult()
                } else {
                    val query = binding.etSearch.text.toString()
                    viewPager.visibility = View.VISIBLE
                    text.visibility = View.GONE
                    searchViewModel.getQuery(query)
                    searchViewModel.getPostSearchResult(query)
                }
            }
        }
    }

}