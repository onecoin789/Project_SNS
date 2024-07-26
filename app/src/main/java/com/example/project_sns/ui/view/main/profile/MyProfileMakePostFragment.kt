package com.example.project_sns.ui.view.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.project_sns.databinding.FragmentMyProfileMakePostBinding

class MyProfileMakePostFragment : Fragment() {

    private var _binding : FragmentMyProfileMakePostBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyProfileMakePostBinding.inflate(inflater, container, false)

        initView()
        
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        binding.ivMakeBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}