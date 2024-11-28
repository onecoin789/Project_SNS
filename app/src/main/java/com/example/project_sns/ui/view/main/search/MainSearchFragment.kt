package com.example.project_sns.ui.view.main.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.project_sns.databinding.FragmentMainSearchBinding
import com.example.project_sns.ui.BaseFragment


class MainSearchFragment : BaseFragment<FragmentMainSearchBinding>() {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainSearchBinding {
        return FragmentMainSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextWatcher()
    }

    private fun editTextWatcher() {
        val searchKeyWords = binding.etSearch.text.toString()

        binding.ivSearchDelete.setOnClickListener {
            binding.etSearch.text.clear()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (binding.etSearch.text.isEmpty()) {
                    binding.clSearchShow.visibility = View.GONE
                } else {
                    binding.clSearchShow.visibility = View.VISIBLE
                    binding.tvSearchWord.text = searchKeyWords
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.etSearch.text.isEmpty()) {
                    binding.clSearchShow.visibility = View.GONE
                } else {
                    binding.clSearchShow.visibility = View.VISIBLE
                    binding.tvSearchWord.text = searchKeyWords
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.etSearch.text.isEmpty()) {
                    binding.clSearchShow.visibility = View.GONE
                } else {
                    binding.clSearchShow.visibility = View.VISIBLE
                    binding.tvSearchWord.text = searchKeyWords
                }
            }

        })
    }

    private fun initSearch() {
        val searchText = binding.etSearch
        binding.ivSearchDelete.setOnClickListener {
            searchText.text.clear()
        }
        searchText.doAfterTextChanged {
            if (searchText.text.isNotEmpty()) {
                binding.clSearchShow.visibility = View.VISIBLE
                val keyword = searchText.text.toString()
                binding.tvSearchWord.text = keyword
            } else {
                binding.clSearchShow.visibility = View.GONE
            }
        }
    }
}