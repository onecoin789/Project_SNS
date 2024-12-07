package com.example.project_sns.ui.view.chat.chatroom

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.databinding.RvItemChatImageBinding
import com.example.project_sns.databinding.RvItemChatPickImageBinding

class ChatRoomImageListAdapter(private val onItemClick: ChatRoomImageClickListener) :
    ListAdapter<Uri, ChatRoomImageListAdapter.ChatRoomImageViewHolder>(diffUtil) {

    interface ChatRoomImageClickListener {
        fun onClickImageCancel(item: Uri)
    }

    class ChatRoomImageViewHolder(
        private val binding: RvItemChatPickImageBinding,
        private val onItemClick: ChatRoomImageClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Uri) {
            binding.ivItemChatImage.clipToOutline = true
            Glide.with(binding.root).load(item).into(binding.ivItemChatImage)

            binding.ivItemChatImageCancel.setOnClickListener {
                onItemClick.onClickImageCancel(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomImageViewHolder {
        val binding = RvItemChatPickImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatRoomImageViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ChatRoomImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Uri>() {
            override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Uri,
                newItem: Uri
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}