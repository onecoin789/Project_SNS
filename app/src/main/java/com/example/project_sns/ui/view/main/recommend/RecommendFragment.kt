package com.example.project_sns.ui.view.main.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.project_sns.databinding.FragmentRecommendBinding
import com.example.project_sns.ui.BaseFragment


class RecommendFragment : BaseFragment<FragmentRecommendBinding>() {



    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRecommendBinding {
        return FragmentRecommendBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }



    private fun initView() {
        binding.ivRecommendBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}