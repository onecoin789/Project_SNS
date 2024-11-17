package com.example.project_sns.ui.view.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemMessageBinding
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.model.MessageModel

class MessageListAdapter(private val onItemClick: MessageItemClickListener) :
    ListAdapter<MessageModel, MessageListAdapter.MessageViewHolder>(diffUtil) {

    interface MessageItemClickListener {

    }

    class MessageViewHolder(
        private val binding: RvItemMessageBinding,
        private val onItemClick: MessageItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun currentUserBind(item: MessageModel) {
            val messageData = item.messageData

            binding.tvItemChatReceiveText.text = messageData.message
            binding.tvItemChatReceiveAt.text = messageData.sendAt

            binding.ivItemItemSendProfile.visibility = View.GONE
            binding.clItemChatSend.visibility = View.GONE
            binding.tvItemChatSendAt.visibility = View.GONE
        }

        fun otherUserBind(item: MessageModel) {
            val userData = item.userData
            val messageData = item.messageData

            if (userData.profileImage != null) {
                Glide.with(binding.root).load(userData.profileImage).into(binding.ivItemItemSendProfile)
            } else {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivItemItemSendProfile)
            }
            binding.ivItemItemSendProfile.clipToOutline = true
            binding.tvItemChatSendText.text = messageData.message
            binding.tvItemChatSendAt.text = messageData.sendAt

            binding.clItemChatReceive.visibility = View.GONE
            binding.tvItemChatReceiveAt.visibility = View.GONE
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = RvItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        if (getItem(position).userData.uid == CurrentUser.userData?.uid) {
            holder.currentUserBind(getItem(position))
        } else {
            holder.otherUserBind(getItem(position))
        }
    }


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
    }
}