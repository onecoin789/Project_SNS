package com.example.project_sns.ui.view.main.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.databinding.RvItemMyPostBinding
import com.example.project_sns.ui.view.model.PostDataModel

class MyProfilePostAdapter(private val onItemClick: (PostDataModel) -> Unit) :
    ListAdapter<PostDataModel, MyProfilePostAdapter.MyPostViewHolder>(diffUtil) {


    class MyPostViewHolder(
        private val binding: RvItemMyPostBinding,
        private val onItemClick: (PostDataModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostDataModel) {
            Glide.with(binding.root).load(item.image).into(binding.ivItemMyPost)
            binding.ivItemMyPost.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostViewHolder {
        val binding = RvItemMyPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyPostViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MyPostViewHolder, position: Int) {
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
