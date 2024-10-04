package com.example.project_sns.ui.view.main.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemFollowBinding
import com.example.project_sns.ui.view.model.RequestModel

class RequestListAdapter(private val onItemClick: FollowItemClickListener) :
    ListAdapter<RequestModel, RequestListAdapter.FollowListViewHolder>(diffUtil) {

    interface FollowItemClickListener {
        fun onClickAcceptButton(item: RequestModel)
        fun onClickRejectButton(item: RequestModel)
    }

    class FollowListViewHolder(
        private val binding: RvItemFollowBinding,
        private val onItemClick: FollowItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: RequestModel) {

            val userData = item.fromUid

            if (userData.profileImage != null) {
                binding.ivItemFollowProfile.clipToOutline = true
                Glide.with(binding.root).load(userData.profileImage).into(binding.ivItemFollowProfile)
            } else {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivItemFollowProfile)
            }

            binding.tvItemFollowName.text = userData.name
            binding.tvItemFollowEmail.text = userData.email

            binding.btnItemFollowAccept.setOnClickListener {
                onItemClick.onClickAcceptButton(item)
            }
            binding.btnItemFollowReject.setOnClickListener {
                onItemClick.onClickRejectButton(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowListViewHolder {
        val binding = RvItemFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowListViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: FollowListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<RequestModel>() {
            override fun areItemsTheSame(oldItem: RequestModel, newItem: RequestModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: RequestModel,
                newItem: RequestModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}