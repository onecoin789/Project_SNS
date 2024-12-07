package com.example.project_sns.ui.view.chat.chatlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemFriendCircleBinding
import com.example.project_sns.ui.model.UserDataModel

class ChatListFriendListAdapter(private val onItemClick: (UserDataModel) -> Unit) :
    ListAdapter<UserDataModel, ChatListFriendListAdapter.CircleFriendListViewHolder>(diffUtil) {

    class CircleFriendListViewHolder(
        private val binding: RvItemFriendCircleBinding,
        private val onItemClick: (UserDataModel) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserDataModel) {
            binding.ivItemFriendCircle.clipToOutline = true
            if (item.profileImage != null) {
                Glide.with(binding.root).load(item.profileImage).into(binding.ivItemFriendCircle)
            } else {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivItemFriendCircle)
            }

            // click listener
            binding.ivItemFriendCircle.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircleFriendListViewHolder {
        val binding = RvItemFriendCircleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CircleFriendListViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: CircleFriendListViewHolder, position: Int) {
        val friendData = getItem(position)
        holder.bind(friendData)
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<UserDataModel>() {
            override fun areItemsTheSame(oldItem: UserDataModel, newItem: UserDataModel): Boolean {
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