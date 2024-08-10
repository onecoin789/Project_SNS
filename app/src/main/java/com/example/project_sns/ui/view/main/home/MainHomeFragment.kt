package com.example.project_sns.ui.view.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMainHomeBinding
import com.example.project_sns.ui.view.main.CommentFragment
import com.example.project_sns.ui.view.main.MainViewModel
import com.example.project_sns.ui.view.main.viewpager.MainFragmentDirections
import com.example.project_sns.ui.view.model.PostDataModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainHomeFragment : Fragment() {

    private var _binding: FragmentMainHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private val mainViewModel : MainViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainHomeBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigateView()
        initRv()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initRv() {
        val postAdapter = HomePostAdapter { data ->
            sendCommentData(data)
        }
        with(binding.rvHome) {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = postAdapter
        }
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.allPostData.collect { list ->
                mainViewModel.getAllPost()
                postAdapter.submitList(list.sortedByDescending { it.createdAt })
            }
        }
    }

    private fun sendCommentData(postData: PostDataModel) {
        val action = MainFragmentDirections.actionMainFragmentToCommentFragment(postData)
        findNavController().navigate(action)

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