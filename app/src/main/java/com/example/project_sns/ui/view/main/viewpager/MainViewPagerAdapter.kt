package com.example.project_sns.ui.view.main.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.project_sns.ui.view.main.home.MainHomeFragment
import com.example.project_sns.ui.view.main.profile.MainMyProfileFragment
import com.example.project_sns.ui.view.main.search.MainSearchFragment

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