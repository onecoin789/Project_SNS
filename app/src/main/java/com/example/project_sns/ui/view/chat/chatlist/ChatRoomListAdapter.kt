package com.example.project_sns.ui.view.chat.chatlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemMessageListBinding
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.model.ChatRoomModel

class ChatRoomListAdapter(private val onItemClick: (ChatRoomModel) -> Unit) :
    ListAdapter<ChatRoomModel, RecyclerView.ViewHolder>(diffUtil) {

    class ChatRoomListViewHolder(
        private val binding: RvItemMessageListBinding,
        private val onItemClick: (ChatRoomModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatRoomModel) {
            val userData = item.userData
            val chatRoomData = item.chatRoomData
            val lastMessageData = item.chatRoomData.lastMessageData

            binding.ivItemListProfile.clipToOutline = true
            if (userData.profileImage != null) {
                Glide.with(binding.root).load(userData.profileImage).into(binding.ivItemListProfile)
            } else {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivItemListProfile)
            }

            binding.tvItemListName.text = userData.name
            binding.tvItemListMessage.text = lastMessageData.lastMessage
            binding.tvItemListTime.text = lastMessageData.lastSendAt

            if (chatRoomData.unReadMessage == 0) {
                binding.clItemListUnReadMessage.visibility = View.GONE
            } else if (lastMessageData.lastMessageSender != CurrentUser.userData?.uid){
                binding.clItemListUnReadMessage.visibility = View.VISIBLE
                binding.tvItemListUnReadMessage.text = chatRoomData.unReadMessage.toString()
            }

            //onClick
            binding.clItemList.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = RvItemMessageListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatRoomListViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatList = getItem(position)
        if (holder is ChatRoomListViewHolder) {
            holder.bind(chatList)
        }
    }
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatRoomModel>() {
            override fun areItemsTheSame(oldItem: ChatRoomModel, newItem: ChatRoomModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ChatRoomModel,
                newItem: ChatRoomModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}

class ItemTouchHelperCallback(
    private val itemMoveListener: OnItemTouchListener
): ItemTouchHelper.Callback() {

    interface OnItemTouchListener {
        fun onSwipeListener()
    }
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.LEFT) {
            itemMoveListener.onSwipeListener()
        }
    }


}