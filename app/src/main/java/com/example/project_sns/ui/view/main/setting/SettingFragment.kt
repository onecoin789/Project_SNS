package com.example.project_sns.ui.view.main.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private var _binding : FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val settingViewModel : SettingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        initView()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {

        binding.ivSettingBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.clSettingLogout.setOnClickListener {
            logout()
            findNavController().navigate(R.id.action_settingFragment_to_loginFragment)
        }
    }

    private fun logout() {
        viewLifecycleOwner.lifecycleScope.launch {
            settingViewModel.logout()
        }
    }
}