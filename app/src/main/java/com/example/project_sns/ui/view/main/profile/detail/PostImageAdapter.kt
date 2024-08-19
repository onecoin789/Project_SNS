package com.example.project_sns.ui.view.main.profile.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.databinding.RvItemImageListBinding

class PostImageAdapter :
    ListAdapter<String, PostImageAdapter.PostImageViewHolder>(diffUtil) {

    class PostImageViewHolder(
        private val binding: RvItemImageListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            Glide.with(binding.root).load(item).into(binding.ivItemImageList)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImageViewHolder {
        val binding = RvItemImageListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostImageViewHolder, position: Int) {
        val imageList = getItem(position)
        holder.bind(imageList)
    }
}