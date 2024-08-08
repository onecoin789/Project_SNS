package com.example.project_sns.ui.view.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemPostPhotoBinding
import com.example.project_sns.ui.view.model.PostDataModel

class HomePostAdapter(private val onItemClick: (PostDataModel) -> Unit) :
    ListAdapter<PostDataModel, HomePostAdapter.HomePostViewHolder>(diffUtil) {

    class HomePostViewHolder(
        private val binding: RvItemPostPhotoBinding,
        private val onItemClick: (PostDataModel) -> Unit
        ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostDataModel) {
            if (item.profileImage != null) {
                binding.ivItemHomeUser.clipToOutline = true
                Glide.with(binding.root).load(item.profileImage).into(binding.ivItemHomeUser)
            } else {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivItemHomeUser)
            }
            Glide.with(binding.root).load(item.image).into(binding.ivItemTitle)
            binding.tvItemHomeEmail.text = item.email
            binding.tvItemHomeName.text = item.name
            binding.tvItemHomePost.text = item.postText
            binding.tvItemHomeComment.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePostViewHolder {
        val binding  = RvItemPostPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomePostViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: HomePostViewHolder, position: Int) {
        val postList = getItem(position)
        holder.bind(postList)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PostDataModel>() {
            override fun areItemsTheSame(oldItem: PostDataModel, newItem: PostDataModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PostDataModel,
                newItem: PostDataModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}