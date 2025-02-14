package com.example.project_sns.ui.view.chat.chatroom

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.project_sns.databinding.RvItemChatImageBinding
import com.example.project_sns.ui.model.ImageDataModel


class MessageImageListAdapter() :
    ListAdapter<ImageDataModel, MessageImageListAdapter.MessageImageListViewHolder>(diffUtil) {

    class MessageImageListViewHolder(
        private val binding: RvItemChatImageBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImageDataModel) {
            Glide.with(binding.root).load(item.downloadUrl).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.pbItemChatImage.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.pbItemChatImage.visibility = View.GONE
                    return false
                }

            }).into(binding.ivItemChatImage)
            binding.ivItemChatImage.clipToOutline = true

            binding.ivItemChatImage.setOnClickListener {
                Log.d("click", "ivItemChatImage")
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageImageListViewHolder {
        val binding = RvItemChatImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageImageListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageImageListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ImageDataModel>() {
            override fun areItemsTheSame(oldItem: ImageDataModel, newItem: ImageDataModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ImageDataModel,
                newItem: ImageDataModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}