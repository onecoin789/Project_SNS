package com.example.project_sns.ui.main.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.project_sns.ui.main.home.MainHomeFragment
import com.example.project_sns.ui.main.profile.MainMyProfileFragment
import com.example.project_sns.ui.main.search.MainSearchFragment

class MainViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    val fgList = listOf(
        MainHomeFragment(),
        MainSearchFragment(),
        MainMyProfileFragment()
    )

    override fun getItemCount(): Int {
        return fgList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fgList[position]
    }


}