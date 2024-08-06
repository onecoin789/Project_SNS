package com.example.project_sns.ui.view.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.project_sns.databinding.RvItemPostPhotoBinding
import com.example.project_sns.ui.view.model.PostDataModel

class HomePostAdapter : ListAdapter<PostDataModel, HomePostAdapter.HomePostViewHolder>(diffUtil) {

    class HomePostViewHolder(private val binding: RvItemPostPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostDataModel) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePostViewHolder {
        return HomePostViewHolder(RvItemPostPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: HomePostViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PostDataModel>() {
            override fun areItemsTheSame(oldItem: PostDataModel, newItem: PostDataModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PostDataModel, newItem: PostDataModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}