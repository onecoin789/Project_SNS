package com.example.project_sns.ui.view.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemPostPhotoBinding
import com.example.project_sns.databinding.RvItemProgressBlackBinding
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.mapper.toViewType
import com.example.project_sns.ui.model.ImageDataModel
import com.example.project_sns.ui.model.PostModel
import com.example.project_sns.ui.view.main.profile.detail.PostImageAdapter

class HomePostAdapter(private val onItemClick: PostItemClickListener) :
    ListAdapter<PostModel, RecyclerView.ViewHolder>(diffUtil) {

    interface PostItemClickListener {
        fun onClickCommentItem(item: PostModel)
        fun onClickProfileImageItem(item: PostModel)
        fun onClickProfileNameItem(item: PostModel)
        fun onClickMapItem(item: PostModel)
        fun onClickLikeButtonItem(item: PostModel)
        fun onClickLikeCancelButtonItem(item: PostModel)
        fun onClickShareButtonItem(item: PostModel)
    }

    class HomePostViewHolder(
        private val binding: RvItemPostPhotoBinding,
        private val onItemClick: PostItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostModel) {

            val postData = item.postData
            val it = item.userData
            val likeButton = binding.ivItemHomeHeart
            val likeCount = binding.tvItemHomeHeartCount
            //Click Event
            binding.tvItemHomeComment.setOnClickListener {
                onItemClick.onClickCommentItem(item)
            }
            binding.ivItemHomeUser.setOnClickListener {
                onItemClick.onClickProfileImageItem(item)
            }
            binding.tvItemHomeName.setOnClickListener {
                onItemClick.onClickProfileNameItem(item)
            }
            binding.ivItemHomeShare.setOnClickListener {
                onItemClick.onClickShareButtonItem(item)
            }
            binding.tvItemHomeLocation.setOnClickListener {
                onItemClick.onClickMapItem(item)
            }

            if (postData.likePost.isNotEmpty()) {
                likeCount.visibility  = View.VISIBLE
                likeCount.text = postData.likePost.size.toString()
            } else {
                likeCount.visibility  = View.GONE
            }



            //좋아요 기능

            if (postData.likePost.contains(CurrentUser.userData?.uid)) {
                likeButton.tag = "1"
            } else {
                likeButton.tag = "0"
            }

            when(likeButton.tag) {
                "0" -> {
                    likeButton.setImageResource(R.drawable.ic_heart)
                }
                "1" -> {
                    likeButton.setImageResource(R.drawable.ic_heart_fill)
                }
            }

            likeButton.setOnClickListener {
                when (likeButton.tag) {
                    "0" -> {
                        likeButton.tag = "1"
                        likeButton.setImageResource(R.drawable.ic_heart_fill)
                        if (!postData.likePost.contains(CurrentUser.userData?.uid)) {
                            likeCount.text = postData.likePost.size.plus(1).toString()
                            if (postData.likePost.isEmpty()) {
                                likeCount.visibility  = View.VISIBLE
                                likeCount.text = "1"
                            }
                        } else if (postData.likePost.contains(CurrentUser.userData?.uid)) {
                            likeCount.text = postData.likePost.size.toString()
                        }
                        onItemClick.onClickLikeButtonItem(item)
                    }

                    "1" -> {
                        likeButton.tag = "0"
                        likeButton.setImageResource(R.drawable.ic_heart)
                        if (postData.likePost.contains(CurrentUser.userData?.uid)) {
                            likeCount.text = postData.likePost.size.minus(1).toString()
                        } else if (!postData.likePost.contains(CurrentUser.userData?.uid)) {
                            likeCount.text = postData.likePost.size.toString()
                            if (likeCount.text == "0") {
                                likeCount.visibility = View.GONE
                            }
                        }
                        onItemClick.onClickLikeCancelButtonItem(item)
                    }
                }
            }


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

            if (postData.editedAt != null) {
                binding.tvItemHomeEdit.visibility = View.VISIBLE
            } else {
                binding.tvItemHomeEdit.visibility = View.GONE
            }


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

    class ProgressViewHolder(binding: RvItemProgressBlackBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM) {
            val binding =
                RvItemPostPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return HomePostViewHolder(binding, onItemClick)
        } else {
            val binding = RvItemProgressBlackBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ProgressViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HomePostViewHolder) {
            holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            null -> LOADING
            else -> ITEM
        }
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
        private const val ITEM = 0
        private const val LOADING = 1
    }
}