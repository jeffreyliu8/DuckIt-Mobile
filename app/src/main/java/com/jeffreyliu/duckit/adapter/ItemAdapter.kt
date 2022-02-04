package com.jeffreyliu.duckit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jeffreyliu.duckit.databinding.ListItemBinding
import com.jeffreyliu.duckit.model.DuckPost


class ItemAdapter(private val listener: ItemClickListener) :
    RecyclerView.Adapter<ItemAdapter.MyViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(device: DuckPost)
        fun onItemLongClick(device: DuckPost)
    }

    private var mDiffer: AsyncListDiffer<DuckPost>
    private val diffCallback: DiffUtil.ItemCallback<DuckPost> =
        object : DiffUtil.ItemCallback<DuckPost>() {
            override fun areItemsTheSame(
                oldItem: DuckPost,
                newItem: DuckPost
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: DuckPost,
                newItem: DuckPost
            ): Boolean {
                return oldItem.author == newItem.author &&
                        oldItem.headline == newItem.headline &&
                        oldItem.image == newItem.image &&
                        oldItem.upVotes == newItem.upVotes
            }
        } //define AsyncListDiffer

    init {
        mDiffer = AsyncListDiffer(this, diffCallback)
    }

    fun updateList(list: List<DuckPost>) {
        mDiffer.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) =
        holder.bind(mDiffer.currentList[position], listener)

    inner class MyViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DuckPost, listener: ItemClickListener) = with(itemView) {
            binding.titleTextView.text = item.headline
            Glide.with(this).load(item.image).into(binding.imageView)
            binding.votesTextView.text = item.upVotes.toString()

            setOnClickListener {
                listener.onItemClick(item)
            }

            setOnLongClickListener {
                listener.onItemLongClick(item)
                return@setOnLongClickListener true
            }
        }
    }
}