package com.example.project_sns.ui.view.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.project_sns.databinding.FragmentMainSearchBinding


class MainSearchFragment : Fragment() {

    private var _binding: FragmentMainSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainSearchBinding.inflate(inflater, container, false)

        initSearch()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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