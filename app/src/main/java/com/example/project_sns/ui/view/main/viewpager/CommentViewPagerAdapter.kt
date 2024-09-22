package com.example.project_sns.ui.view.main.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.project_sns.ui.view.main.comment.CommentListFragment
import com.example.project_sns.ui.view.main.comment.ReCommentListFragment

class CommentViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private val fragmentList = listOf(CommentListFragment(), ReCommentListFragment())

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}