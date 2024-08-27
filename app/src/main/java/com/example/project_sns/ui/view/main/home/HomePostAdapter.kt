package com.example.project_sns.ui.view.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemPostPhotoBinding
import com.example.project_sns.ui.mapper.toViewType
import com.example.project_sns.ui.view.main.profile.detail.PostImageAdapter
import com.example.project_sns.ui.view.model.ImageDataModel
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
            binding.tvItemHomeEmail.text = item.email
            binding.tvItemHomeName.text = item.name
            binding.tvItemHomePost.text = item.postText
            binding.tvItemHomeComment.setOnClickListener {
                onItemClick(item)
            }

            if (item.mapData?.placeName != null) {
                binding.tvItemHomeLocation.text = item.mapData.placeName
            }
            initRv(item.imageList)

            if (item.imageList?.size == 1) {
                binding.idcItem.visibility = View.INVISIBLE
            } else {
                binding.idcItem.visibility = View.VISIBLE
            }
        }

        private fun initRv(imageData : List<ImageDataModel>?) {
            val imageAdapter = PostImageAdapter()
            val indicator = binding.idcItem
            val listViewType = imageData?.map { it.toViewType("video") }
            imageAdapter.submitList(listViewType)
            with(binding.vpItemTitle) {
                adapter = imageAdapter
            }
            indicator.attachTo(binding.vpItemTitle)

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