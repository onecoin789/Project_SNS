package com.example.project_sns.ui.view.main.profile.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.databinding.RvItemImageListBinding
import com.example.project_sns.databinding.RvItemVideoBinding
import com.example.project_sns.ui.util.PostImageType
import com.example.project_sns.ui.util.getType
import com.example.project_sns.ui.view.model.ImageDataModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class PostImageAdapter :
    ListAdapter<PostImageType, RecyclerView.ViewHolder>(diffUtil) {

    class PostImageViewHolder(
        private val binding: RvItemImageListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageDataModel) {
            Glide.with(binding.root).load(item.downloadUrl).into(binding.ivItemImageList)
        }
    }

    class PostVideoViewHolder(
        private val binding: RvItemVideoBinding,
        private var exoPlayer: ExoPlayer
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageDataModel) {

            val mediaItem = MediaItem.fromUri(item.downloadUrl)
            binding.epItemVideo.player = exoPlayer
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }

        fun startPlayer() {
            exoPlayer.prepare()
//            exoPlayer.playWhenReady = true
//            exoPlayer.play()
        }

        fun stopPlayer() {
            exoPlayer.stop()
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
                RvItemVideoBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                ), ExoPlayer.Builder(parent.context).build()
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


    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)

        when (holder) {
            is PostVideoViewHolder -> holder.stopPlayer()
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)

        when (holder) {
            is PostVideoViewHolder -> holder.startPlayer()
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

