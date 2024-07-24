package com.example.project_sns.ui.main.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        initViewPager()
        initBottomBar()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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