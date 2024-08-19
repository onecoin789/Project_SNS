package com.example.project_sns.ui.view.main.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMainBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.view.main.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {

    private val mainViewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel.getCurrentUserData()

    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewPager()
        initBottomBar()
    }

    private fun initViewPager() {
        binding.vpMain.isUserInputEnabled = false
        val viewPager = binding.vpMain
        val tabLayout = binding.tlMainBottom
        viewPager.adapter = MainViewPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.ic_home)
                1 -> tab.setIcon(R.drawable.ic_search)
                2 -> tab.setIcon(R.drawable.ic_user)
            }
        }.attach()

        binding.tlMainBottom.tabTextColors =
            ContextCompat.getColorStateList(requireContext(), R.color.tab_selector)
        binding.tlMainBottom.tabIconTint =
            ContextCompat.getColorStateList(requireContext(), R.color.tab_selector)
    }

    private fun initBottomBar() {
        with(binding) {
            vpMain.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> {}
                        1 -> {}
                        2 -> {}
                    }
                }
            })
        }
    }


}