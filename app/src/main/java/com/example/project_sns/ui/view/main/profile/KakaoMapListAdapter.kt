package com.example.project_sns.ui.view.main.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.project_sns.databinding.RvItemMapListBinding
import com.example.project_sns.ui.view.model.KakaoDocumentsModel

class KakaoMapListAdapter(private val onItemClick: (KakaoDocumentsModel) -> Unit) :
    ListAdapter<KakaoDocumentsModel, KakaoMapListAdapter.MapListViewHolder>(diffUtil) {

    class MapListViewHolder(
        private val binding: RvItemMapListBinding,
        private val onItemClick: (KakaoDocumentsModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: KakaoDocumentsModel) {

            binding.tvItemMapName.text = item.placeName
            binding.tvItemMapAddress.text = item.addressName
            binding.tvItemMapNumber.text = item.phone

            binding.clItemMap.setOnClickListener {
                onItemClick(item)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapListViewHolder {
        val binding =
            RvItemMapListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MapListViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MapListViewHolder, position: Int) {
        val mapList = getItem(position)
        holder.bind(mapList)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<KakaoDocumentsModel>() {
            override fun areItemsTheSame(
                oldItem: KakaoDocumentsModel,
                newItem: KakaoDocumentsModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: KakaoDocumentsModel,
                newItem: KakaoDocumentsModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
