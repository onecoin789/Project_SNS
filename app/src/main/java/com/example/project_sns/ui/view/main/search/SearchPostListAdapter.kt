package com.example.project_sns.ui.view.main.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.databinding.RvItemSearchPostBinding
import com.example.project_sns.ui.model.PostDataModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem


class SearchPostListAdapter(private val onItemClick: (PostDataModel) -> Unit) :
    ListAdapter<PostDataModel, SearchPostListAdapter.SearchPostViewHolder>(diffUtil) {

    class SearchPostViewHolder(
        private val binding: RvItemSearchPostBinding,
        private val exoPlayer: ExoPlayer,
        private val onItemClick: (PostDataModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostDataModel) {
            binding.epItemSearchVideo.setOnClickListener {
                onItemClick(item)
            }
            binding.clItemSearchImageFrame.setOnClickListener {
                onItemClick(item)
            }

            if (!item.imageList.isNullOrEmpty()) {
                val firstImage = item.imageList[0]
                if (firstImage.imageType == "image") {
                    binding.ivItemSearchImage.visibility = View.VISIBLE
                    binding.clItemSearchImageFrame.visibility = View.VISIBLE
                    binding.epItemSearchVideo.visibility = View.GONE
                    binding.clItemSearchVideoFrame.visibility = View.GONE

                    Glide.with(binding.root).load(firstImage.downloadUrl).into(binding.ivItemSearchImage)
                } else if (firstImage.imageType == "video") {
                    binding.epItemSearchVideo.visibility = View.VISIBLE
                    binding.clItemSearchVideoFrame.visibility = View.VISIBLE
                    binding.ivItemSearchImage.visibility = View.GONE
                    binding.clItemSearchImageFrame.visibility = View.GONE

                    val mediaItem = MediaItem.fromUri(firstImage.downloadUrl)
                    binding.epItemSearchVideo.player = exoPlayer
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.volume = 0F
                    exoPlayer.play()

                }
            }
        }

        fun startPlayer() {
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            exoPlayer.play()
        }

        fun stopPlayer() {
            exoPlayer.stop()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPostViewHolder {
        val binding = RvItemSearchPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchPostViewHolder(binding, ExoPlayer.Builder(parent.context).build(), onItemClick)
    }

    override fun onBindViewHolder(holder: SearchPostViewHolder, position: Int) {
        val postList = getItem(position)
        holder.bind(postList)
    }

    override fun onViewDetachedFromWindow(holder: SearchPostViewHolder) {
        super.onViewDetachedFromWindow(holder)

        holder.stopPlayer()

    }

    override fun onViewAttachedToWindow(holder: SearchPostViewHolder) {
        super.onViewAttachedToWindow(holder)

        holder.startPlayer()

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
        private const val IMAGE_VIEW = 1
        private const val VIDEO_VIEW = 2
    }
}