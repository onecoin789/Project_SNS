package com.example.project_sns.ui.view.main.profile.detail

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.databinding.RvItemImageListBinding
import com.example.project_sns.databinding.RvItemVideoListBinding
import com.example.project_sns.ui.view.main.profile.PostImageType
import com.example.project_sns.ui.view.main.profile.getType
import com.example.project_sns.ui.view.model.ImageDataModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.withContext

class PostImageAdapter :
    ListAdapter<PostImageType, RecyclerView.ViewHolder>(diffUtil) {

    class PostImageViewHolder(
        private val binding: RvItemImageListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageDataModel) {
            Glide.with(binding.root).load(item.imageUri).into(binding.ivItemImageList)
            Log.d("test_viewHolder", "$item")
        }
    }

    class PostVideoViewHolder(
        private val binding: RvItemVideoListBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageDataModel) {

            val exoPlayer = ExoPlayer.Builder(context).build()
            val mediaItem = MediaItem.fromUri(item.imageUri)
            binding.epItemVideoList.player = exoPlayer
            binding.epItemVideoList.controllerShowTimeoutMs = 0
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            IMAGE_VIEW -> PostImageViewHolder(
                RvItemImageListBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            VIDEO_VIEW -> PostVideoViewHolder(
                RvItemVideoListBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                ), parent.context
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostImageViewHolder -> holder.bind((getItem(position) as PostImageType.PostImage).type)
            is PostVideoViewHolder -> holder.bind((getItem(position) as PostImageType.PostVideo).type)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PostImageType.PostImage -> IMAGE_VIEW
            is PostImageType.PostVideo -> VIDEO_VIEW
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PostImageType>() {
            override fun areItemsTheSame(oldItem: PostImageType, newItem: PostImageType): Boolean {
                return oldItem.getType() == newItem.getType()
            }

            override fun areContentsTheSame(
                oldItem: PostImageType,
                newItem: PostImageType
            ): Boolean {
                return oldItem == newItem
            }
        }
        private const val IMAGE_VIEW = 1
        private const val VIDEO_VIEW = 2
    }
}