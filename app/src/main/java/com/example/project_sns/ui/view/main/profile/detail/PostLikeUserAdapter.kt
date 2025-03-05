package com.example.project_sns.ui.view.main.profile.detail

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

class PostLikeUserAdapter(private val onItemClick: LikeUserItemClickListener):
    ListAdapter<UserDataModel, RecyclerView.ViewHolder>(diffUtil) {

    interface LikeUserItemClickListener {
//        fun onClickFriendProfile(item: UserDataModel)
//        fun onClickFriendName(item: UserDataModel)
    }

    class MyProfileFriendViewHolder(
        private val binding: RvItemFriendBinding,
        private val onClick: LikeUserItemClickListener
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
//            binding.ivItemFriendProfile.setOnClickListener {
//                onClick.onClickFriendProfile(item)
//            }
//            binding.tvItemFriendName.setOnClickListener {
//                onClick.onClickFriendName(item)
//            }

            binding.ivItemFriendDelete.visibility = View.GONE
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
    }
}