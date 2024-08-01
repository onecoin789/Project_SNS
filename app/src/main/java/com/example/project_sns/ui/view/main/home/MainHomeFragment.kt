package com.example.project_sns.ui.view.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentHomeBinding
import com.example.project_sns.ui.CurrentUser
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainHomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        initView()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
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

    private fun initData() {

    }
}