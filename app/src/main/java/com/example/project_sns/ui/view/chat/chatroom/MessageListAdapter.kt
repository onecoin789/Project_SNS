package com.example.project_sns.ui.view.chat.chatroom

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemImageMessageBinding
import com.example.project_sns.databinding.RvItemTextMessageBinding
import com.example.project_sns.domain.MessageViewType
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.model.ImageDataModel
import com.example.project_sns.ui.model.MessageModel

class MessageListAdapter(private val onItemClick: MessageItemClickListener) :
    ListAdapter<MessageModel, RecyclerView.ViewHolder>(diffUtil) {

    interface MessageItemClickListener {
        fun onChatImageClickEvent(item: MessageModel)
    }

    class TextMessageViewHolder(
        private val binding: RvItemTextMessageBinding,
        private val onItemClick: MessageItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun currentUserBind(item: MessageModel) {
            val messageData = item.messageData

            val unreadSize = messageData.read.count {
                it.containsValue(false)
            }

            binding.tvItemChatReceiveText.text = messageData.message
            binding.tvItemChatReceiveAt.text = messageData.sendAt

            binding.ivItemSenderProfile.visibility = View.GONE
            binding.clItemChatSend.visibility = View.GONE
            binding.tvItemChatSendAt.visibility = View.GONE
            binding.tvItemSenderName.visibility = View.GONE

            if (unreadSize != 0) {
                binding.tvItemChatReceiveUnRead.visibility = View.VISIBLE
                binding.tvItemChatReceiveUnRead.text = unreadSize.toString()
            } else {
                binding.tvItemChatReceiveUnRead.visibility = View.GONE
            }

        }

        fun otherUserBind(item: MessageModel) {
            val userData = item.userData
            val messageData = item.messageData

            if (userData == null) {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivItemSenderProfile)
                binding.tvItemSenderName.text = "탈퇴한 사용자"
                binding.tvItemChatSendText.text = messageData.message
                binding.tvItemChatSendAt.text = messageData.sendAt
                binding.clItemChatReceive.visibility = View.GONE
                binding.tvItemChatReceiveAt.visibility = View.GONE
            } else {
                if (userData.profileImage != null) {
                    Glide.with(binding.root).load(userData.profileImage).into(binding.ivItemSenderProfile)
                } else {
                    Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivItemSenderProfile)
                }
                binding.ivItemSenderProfile.clipToOutline = true

                binding.tvItemSenderName.text = userData.name
                binding.tvItemChatSendText.text = messageData.message
                binding.tvItemChatSendAt.text = messageData.sendAt

                binding.clItemChatReceive.visibility = View.GONE
                binding.tvItemChatReceiveAt.visibility = View.GONE
            }

        }
    }

    class ImageMessageViewHolder(
        private val binding: RvItemImageMessageBinding,
        private val onItemClick: MessageItemClickListener
    ): RecyclerView.ViewHolder(binding.root) {
        fun currentUserBind(item: MessageModel) {
            val messageData = item.messageData

            val unreadSize = messageData.read.count {
                it.containsValue(false)
            }

            if (messageData.message == "imageListLoading") {
                binding.pbItemChatReceiveImageLoading.visibility = View.VISIBLE
                binding.rvItemChatReceiveImage.visibility = View.GONE
            } else {
                binding.pbItemChatReceiveImageLoading.visibility = View.GONE
                binding.rvItemChatReceiveImage.visibility = View.VISIBLE
            }

            if (messageData.imageList != null) {
                val imageSize = messageData.imageList.size
                if (imageSize <= 3) {
                    initCurrentUserRv(messageData.imageList, imageSize)
                } else {
                    initCurrentUserRv(messageData.imageList, 3)
                }
            }
            binding.tvItemChatReceiveAt.text = messageData.sendAt

            binding.clItemChatSend.visibility = View.GONE
            binding.tvItemChatSendAt.visibility = View.GONE
            binding.tvItemSenderName.visibility = View.GONE

            if (unreadSize != 0) {
                binding.tvItemChatReceiveUnRead.visibility = View.VISIBLE
                binding.tvItemChatReceiveUnRead.text = unreadSize.toString()
            } else {
                binding.tvItemChatReceiveUnRead.visibility = View.GONE
            }

            //click event
            binding.clItemChatReceiveClick.setOnClickListener {
                onItemClick.onChatImageClickEvent(item)
                Log.d("click", "clItemChatReceive")
            }
        }

        fun otherUserBind(item: MessageModel) {
            val userData = item.userData
            val messageData = item.messageData

            if (userData?.profileImage != null) {
                Glide.with(binding.root).load(userData.profileImage).into(binding.ivItemSenderProfile)
            } else {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivItemSenderProfile)
            }
            binding.ivItemSenderProfile.clipToOutline = true

            if (messageData.message == "imageListLoading") {
                binding.pbItemChatSendImageLoading.visibility = View.VISIBLE
                binding.rvItemChatSendImage.visibility = View.GONE
            } else {
                binding.pbItemChatSendImageLoading.visibility = View.GONE
                binding.rvItemChatSendImage.visibility = View.VISIBLE
            }

            if (userData == null) {
                binding.tvItemSenderName.text = "탈퇴한 사용자"
                binding.tvItemChatSendAt.text = messageData.sendAt
                binding.clItemChatReceive.visibility = View.GONE
                binding.tvItemChatReceiveAt.visibility = View.GONE
            } else {
                if (messageData.imageList != null) {
                    val imageSize = messageData.imageList.size
                    if (messageData.imageList.size <= 3) {
                        initOtherUserRv(messageData.imageList, imageSize)
                    } else {
                        initOtherUserRv(messageData.imageList, 3)
                    }
                }
                binding.tvItemSenderName.text = userData.name
                binding.tvItemChatSendAt.text = messageData.sendAt

                binding.clItemChatReceive.visibility = View.GONE
                binding.tvItemChatReceiveAt.visibility = View.GONE
            }


            //click event
            binding.clItemChatSendClick.setOnClickListener {
                onItemClick.onChatImageClickEvent(item)
            }
        }

        private fun initCurrentUserRv(imageList: List<ImageDataModel>, itemSize: Int) {
            val imageAdapter = MessageImageListAdapter()
            with(binding.rvItemChatReceiveImage) {
                adapter = imageAdapter
                layoutManager = GridLayoutManager(context, itemSize)
            }
            imageAdapter.submitList(imageList)
        }

        private fun initOtherUserRv(imageList: List<ImageDataModel>, itemSize: Int) {
            val imageAdapter = MessageImageListAdapter()
            with(binding.rvItemChatSendImage) {
                adapter = imageAdapter
                layoutManager = GridLayoutManager(context, itemSize)
            }
            imageAdapter.submitList(imageList)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            MessageViewType.TEXT_MESSAGE.messageType -> {
                val binding = RvItemTextMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TextMessageViewHolder(binding, onItemClick)
            }
            MessageViewType.IMAGE_MESSAGE.messageType -> {
                val binding = RvItemImageMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ImageMessageViewHolder(binding, onItemClick)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item.userData?.uid == CurrentUser.userData?.uid) {
            when(holder.itemViewType) {
                MessageViewType.TEXT_MESSAGE.messageType -> {
                    val textMessageType = holder as TextMessageViewHolder
                    textMessageType.currentUserBind(item)
                }
                MessageViewType.IMAGE_MESSAGE.messageType -> {
                    val imageMessageType = holder as ImageMessageViewHolder
                    imageMessageType.currentUserBind(item)
                }
            }
        } else {
            when(holder.itemViewType) {
                MessageViewType.TEXT_MESSAGE.messageType -> {
                    val textMessageType = holder as TextMessageViewHolder
                    textMessageType.otherUserBind(item)
                }
                MessageViewType.IMAGE_MESSAGE.messageType -> {
                    val imageMessageType = holder as ImageMessageViewHolder
                    imageMessageType.otherUserBind(item)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position).messageData.type.messageType) {
            0 -> MessageViewType.TEXT_MESSAGE.messageType
            1 -> MessageViewType.IMAGE_MESSAGE.messageType
            else -> throw IllegalArgumentException("$position")
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