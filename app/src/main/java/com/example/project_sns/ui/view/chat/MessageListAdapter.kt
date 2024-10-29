package com.example.project_sns.ui.view.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemReceiveBinding
import com.example.project_sns.databinding.RvItemSendBinding
import com.example.project_sns.ui.model.MessageModel

class MessageListAdapter(private val onItemClick: MessageItemClickListener) :
    ListAdapter<MessageModel, RecyclerView.ViewHolder>(diffUtil) {

        interface MessageItemClickListener {

        }

    class SenderMessageViewHolder(
        private val binding: RvItemSendBinding,
        private val onItemClick: MessageItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MessageModel) {
            val userData = item.userData
            val messageData = item.messageData

            binding.ivItemItemSendProfile.clipToOutline = true
            if (userData.profileImage != null) {
                Glide.with(binding.root).load(userData.profileImage).into(binding.ivItemItemSendProfile)
            } else {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivItemItemSendProfile)
            }

            binding.tvItemSendText.text = messageData.message
            binding.tvItemChatSendAt.text = messageData.sendAt
        }
    }

    class ReceiveMessageViewHolder(
        private val binding: RvItemReceiveBinding,
        private val onItemClick: MessageItemClickListener
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MessageModel) {
            val messageData = item.messageData

            binding.tvItemChatReceiveText.text = messageData.message
            binding.tvItemChatReceiveAt.text = messageData.sendAt
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == SEND) {
            val binding = RvItemSendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SenderMessageViewHolder(binding, onItemClick)
        } else {
            val binding = RvItemReceiveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ReceiveMessageViewHolder(binding, onItemClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SenderMessageViewHolder) {
            holder.bind(getItem(position))
        } else if (holder is ReceiveMessageViewHolder) {
            holder.bind(getItem(position))
        }
    }

//    override fun getItemViewType(position: Int): Int {
//        return when (getItem(position)) {
//
//        }
//    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MessageModel>() {
            override fun areItemsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: MessageModel,
                newItem: MessageModel
            ): Boolean {
                return oldItem == newItem
            }
        }
        private const val SEND = 0
        private const val RECEIVE = 1
    }
}