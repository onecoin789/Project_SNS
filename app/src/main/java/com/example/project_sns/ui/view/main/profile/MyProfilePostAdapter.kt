package com.example.project_sns.ui.view.main.profile

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
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemMyPostBinding
import com.example.project_sns.ui.model.PostDataModel

class MyProfilePostAdapter(private val onItemClick: (PostDataModel) -> Unit) :
    ListAdapter<PostDataModel, MyProfilePostAdapter.MyPostViewHolder>(diffUtil) {


    class MyPostViewHolder(
        private val binding: RvItemMyPostBinding,
        private val onItemClick: (PostDataModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostDataModel) {
            if (item.imageList.isNullOrEmpty()) {
                Glide.with(binding.root).load(item.imageList?.map { it.imageUri }).into(binding.ivItemMyPost)
            } else {
                Glide.with(binding.root).load(item.imageList[0].downloadUrl).listener(object :
                    RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.clItemMyPostLoading.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.clItemMyPostLoading.visibility = View.GONE
                        return false
                    }


                }).into(binding.ivItemMyPost)
            }

            if (item.imageList?.size == 1) {
                binding.ivItemMyPostMultiple.visibility = View.GONE
            } else {
                binding.ivItemMyPostMultiple.visibility = View.VISIBLE
            }

            binding.ivItemMyPost.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostViewHolder {
        val binding = RvItemMyPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyPostViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MyPostViewHolder, position: Int) {
        val postList = getItem(position)
        holder.bind(postList)
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
    }
}
