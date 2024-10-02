package com.example.project_sns.ui.view.main.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemFriendBinding
import com.example.project_sns.ui.view.model.UserDataModel

class MyProfileFriendAdapter(private val onItemClick: MyProfileFriendItemClickListener):
    ListAdapter<UserDataModel, RecyclerView.ViewHolder>(diffUtil) {

    interface MyProfileFriendItemClickListener {
        fun onClickFriendProfile(item: UserDataModel)
        fun onClickFriendName(item: UserDataModel)
        fun onClickFriendDelete(item: UserDataModel)
    }

    class MyProfileFriendViewHolder(
        private val binding: RvItemFriendBinding,
        private val onClick: MyProfileFriendItemClickListener
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserDataModel) {
            if (item.profileImage != null) {
                binding.ivItemFriendProfile.clipToOutline = true
                Glide.with(binding.root).load(item.profileImage).into(binding.ivItemFriendProfile)
            } else {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivItemFriendProfile)
            }
            binding.tvItemFriendName.text = item.name
            binding.tvItemFriendEmail.text = item.email

            //click event
            binding.ivItemFriendProfile.setOnClickListener {
                onClick.onClickFriendProfile(item)
            }
            binding.tvItemFriendName.setOnClickListener {
                onClick.onClickFriendName(item)
            }
            binding.ivItemFriendDelete.setOnClickListener {
                onClick.onClickFriendDelete(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = RvItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyProfileFriendViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyProfileFriendViewHolder) {
            holder.bind(getItem(position))
        }
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
        private const val ITEM = 0
        private const val LOADING = 1
    }
}