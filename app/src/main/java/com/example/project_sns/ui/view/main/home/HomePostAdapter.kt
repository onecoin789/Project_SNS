package com.example.project_sns.ui.view.main.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemPostPhotoBinding
import com.example.project_sns.ui.mapper.toViewType
import com.example.project_sns.ui.view.main.profile.detail.PostImageAdapter
import com.example.project_sns.ui.view.model.ImageDataModel
import com.example.project_sns.ui.view.model.PostModel

class HomePostAdapter(private val onItemClick: (PostModel) -> Unit) :
    ListAdapter<PostModel, HomePostAdapter.HomePostViewHolder>(diffUtil) {

    class HomePostViewHolder(
        private val binding: RvItemPostPhotoBinding,
        private val onItemClick: (PostModel) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostModel) {

            val postData = item.postData
            val it = item.userData

            binding.tvItemHomeComment.setOnClickListener {
                onItemClick(item)
            }
            Log.d("test_data", postData.uid)
            binding.tvItemHomePost.text = postData.postText
            if (postData.mapData?.placeName != null) {
                binding.tvItemHomeLocation.text = postData.mapData.placeName
            }
            initRv(postData.imageList)
            if (postData.imageList?.size == 1) {
                binding.idcItem.visibility = View.INVISIBLE
            } else {
                binding.idcItem.visibility = View.VISIBLE
            }


            Log.d("test_data", it.uid)
            if (it.profileImage != null) {
                binding.ivItemHomeUser.clipToOutline = true
                Glide.with(binding.root).load(it.profileImage)
                    .into(binding.ivItemHomeUser)
            } else {
                Glide.with(binding.root).load(R.drawable.ic_user_fill)
                    .into(binding.ivItemHomeUser)
            }
            binding.tvItemHomeEmail.text = it.email
            binding.tvItemHomeName.text = it.name

        }


        private fun initRv(imageData: List<ImageDataModel>?) {
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
        val binding =
            RvItemPostPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomePostViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: HomePostViewHolder, position: Int) {
        val postList = getItem(position) ?: return
        holder.bind(postList)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PostModel>() {
            override fun areItemsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PostModel,
                newItem: PostModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}