package com.example.project_sns.ui.view.main.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemFriendBinding
import com.example.project_sns.ui.model.UserDataModel

class SearchUserListAdapter(private val onItemClick: (UserDataModel) -> Unit):
ListAdapter<UserDataModel, SearchUserListAdapter.SearchUserViewHolder>(diffUtil) {

    class SearchUserViewHolder(
        private val binding: RvItemFriendBinding,
        private val onItemClick: (UserDataModel) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserDataModel) {

            binding.ivItemFriendProfile.clipToOutline = true

            if (item.profileImage != null) {
                Glide.with(binding.root).load(item.profileImage).into(binding.ivItemFriendProfile)
            } else {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivItemFriendProfile)
            }

            binding.tvItemFriendName.text = item.name
            binding.tvItemFriendEmail.text = item.email

            binding.ivItemFriendDelete.visibility = View.GONE


            binding.clItemFriend.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUserViewHolder {
        val binding = RvItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchUserViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: SearchUserViewHolder, position: Int) {
        val userList = getItem(position)
        holder.bind(userList)
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<UserDataModel>() {
            override fun areItemsTheSame(
                oldItem: UserDataModel,
                newItem: UserDataModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: UserDataModel,
                newItem: UserDataModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}