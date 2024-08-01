package com.example.project_sns.ui.view.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.project_sns.databinding.RvItemPostPhotoBinding
import com.example.project_sns.ui.view.data.PostData

class HomePostAdapter : ListAdapter<PostData, HomePostAdapter.HomePostViewHolder>(diffUtil) {

    class HomePostViewHolder(private val binding: RvItemPostPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostData) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePostViewHolder {
        return HomePostViewHolder(RvItemPostPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: HomePostViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PostData>() {
            override fun areItemsTheSame(oldItem: PostData, newItem: PostData): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PostData, newItem: PostData): Boolean {
                return oldItem == newItem
            }

        }
    }
}