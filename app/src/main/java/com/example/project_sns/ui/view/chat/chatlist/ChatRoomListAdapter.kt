package com.example.project_sns.ui.view.chat.chatlist

import android.content.ClipData.Item
import android.graphics.Canvas
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemMessageListBinding
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.model.ChatRoomModel
import kotlin.math.max
import kotlin.math.min

class ChatRoomListAdapter(private val onItemClick: ChatRoomListClickListener) :
    ListAdapter<ChatRoomModel, RecyclerView.ViewHolder>(diffUtil) {

    interface ChatRoomListClickListener {
        fun onListClickListener(data: ChatRoomModel)
        fun onListDeleteClickListener(data: ChatRoomModel)
    }

    class ChatRoomListViewHolder(
        private val binding: RvItemMessageListBinding,
        private val onItemClick: ChatRoomListClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatRoomModel) {
            val userData = item.userData
            val chatRoomData = item.chatRoomData
            val lastMessageData = item.chatRoomData.lastMessageData

            if (userData == null) {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivItemListProfile)
                binding.tvItemListName.text = "탈퇴한 사용자"
                binding.tvItemListMessage.text = lastMessageData.lastMessage
                binding.tvItemListTime.text = lastMessageData.lastSendAt
            } else {
                if (userData.profileImage != null) {
                    Glide.with(binding.root).load(userData.profileImage).into(binding.ivItemListProfile)
                } else {
                    Glide.with(binding.root).load(R.drawable.ic_user_fill)
                        .into(binding.ivItemListProfile)
                }

                binding.tvItemListName.text = userData.name
                binding.tvItemListMessage.text = lastMessageData.lastMessage
                binding.tvItemListTime.text = lastMessageData.lastSendAt
            }

            binding.ivItemListProfile.clipToOutline = true

            binding.clItemList.translationX = 0f


            if (chatRoomData.unReadMessage == 0) {
                binding.clItemListUnReadMessage.visibility = View.GONE
            } else if (lastMessageData.lastMessageSender != CurrentUser.userData?.uid) {
                binding.clItemListUnReadMessage.visibility = View.VISIBLE
                binding.tvItemListUnReadMessage.text = chatRoomData.unReadMessage.toString()
            }

            //onClick
            binding.clItemList.setOnClickListener {
                onItemClick.onListClickListener(item)
            }
            binding.clItemListDelete.setOnClickListener {
                onItemClick.onListDeleteClickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            RvItemMessageListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

class ItemTouchHelperCallback : ItemTouchHelper.Callback() {

    private var currentPosition: Int? = null
    private var previousPosition: Int? = null
    private var currentDx = 0f
    private var clamp = 0f


    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
        return makeMovementFlags(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.LEFT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        currentDx = 0f
        previousPosition = viewHolder.bindingAdapterPosition
        getDefaultUIUtil().clearView(getView(viewHolder))
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        viewHolder?.let {
            currentPosition = viewHolder.bindingAdapterPosition
            clamp = getViewWidth(viewHolder as ChatRoomListAdapter.ChatRoomListViewHolder)
            getDefaultUIUtil().onSelected(getView(it))
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val view = getView(viewHolder)
            val isClamped = getTag(viewHolder)
            val newX = clampViewPositionHorizontal(dX, isClamped, isCurrentlyActive)

            if (newX == -clamp) {
                getView(viewHolder).animate().translationX(-clamp).setDuration(100L).start()
                (viewHolder as ChatRoomListAdapter.ChatRoomListViewHolder).itemView.findViewById<ConstraintLayout>(R.id.clItemListClose).setOnClickListener {
                    getView(viewHolder).animate().translationX(0f).setDuration(100L).start()
                }
                return
            }

            currentDx = newX

            getDefaultUIUtil().onDraw(
                c, recyclerView, view, dX, dY, actionState, isCurrentlyActive
            )
        }


        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    private fun getViewWidth(viewHolder: RecyclerView.ViewHolder): Float{
        val viewWidth = (viewHolder as ChatRoomListAdapter.ChatRoomListViewHolder).itemView.findViewById<ConstraintLayout>(R.id.clItemListSwipe).width
        return viewWidth.toFloat()
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 10
    }

    override fun getSwipeThreshold(viewHolder: ViewHolder): Float {
        setTag(viewHolder, currentDx <= -clamp)
        return 2f
    }

//    // 삭제버튼 width 구하는 함수
//    private fun getViewWidth(viewHolder: ViewHolder): Float {
//        val viewWidth =
//            (viewHolder as ChatRoomListAdapter.ChatRoomListViewHolder).itemView.findViewById<ConstraintLayout>(
//                R.id.clItemListDelete
//            ).width
//        return viewWidth.toFloat()
//    }

    // swipe 될 뷰 (우리가 스와이프 시 움직일 화면)
    private fun getView(viewHolder: ViewHolder): View {
        return (viewHolder as ChatRoomListAdapter.ChatRoomListViewHolder).itemView.findViewById(R.id.clItemList)
    }

    // view의 tag로 스와이프 고정됐는지 안됐는지 확인 (고정 == true)
    private fun getTag(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder.itemView.tag as? Boolean ?: false
    }

    // view의 tag에 스와이프 고정됐으면 true, 안됐으면 false 값 넣기
    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        viewHolder.itemView.tag = isClamped
    }

    // 스와이프 될 가로(수평평) 길이
    private fun clampViewPositionHorizontal(
        dX: Float,  //
        isClamped: Boolean,
        isCurrentlyActive: Boolean
    ): Float {
        val maxSwipe: Float = -clamp * 1.5f

        val right = 0f

        val x = if (isClamped) {
            if (isCurrentlyActive) dX - clamp else -clamp
        } else dX

        return min(
            max(maxSwipe, x),
            right
        )
//        val max = 0f
//
//        // 고정할 수 있으면
//        val newX = if (isClamped) {
//            // 현재 swipe 중이면 swipe되는 영역 제한
//            if (isCurrentlyActive)
//            // 오른쪽 swipe일 때
//                if (dX < 0) dX/3 - clamp
//                // 왼쪽 swipe일 때
//                else dX - clamp
//            // swipe 중이 아니면 고정시키기
//            else -clamp
//        }
//        // 고정할 수 없으면 newX는 스와이프한 만큼
//        else dX / 2
//
//        // newX가 0보다 작은지 확인
//        return min(newX, max)
    }

    fun removePreviousClamp(recyclerView: RecyclerView) {
        if (currentPosition == previousPosition)
            return
        previousPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
            getView(viewHolder).animate().x(0f).setDuration(100L).start()
            setTag(viewHolder, false)
            previousPosition = null
        }
    }


}