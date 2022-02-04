package com.jeffreyliu.duckit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jeffreyliu.duckit.databinding.ListItemBinding
import com.jeffreyliu.duckit.model.DuckPost
import com.jeffreyliu.duckit.model.DuckPostLoggedInWrapper


class ItemAdapter(private var listener: ItemClickListener?) :
    RecyclerView.Adapter<ItemAdapter.MyViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(post: DuckPost)
        fun onItemLongClick(post: DuckPost)
        fun onUpVote(post: DuckPost)
        fun onDownVote(post: DuckPost)
    }

    private var mDiffer: AsyncListDiffer<DuckPostLoggedInWrapper>
    private val diffCallback: DiffUtil.ItemCallback<DuckPostLoggedInWrapper> =
        object : DiffUtil.ItemCallback<DuckPostLoggedInWrapper>() {
            override fun areItemsTheSame(
                oldItem: DuckPostLoggedInWrapper,
                newItem: DuckPostLoggedInWrapper
            ): Boolean {
                return oldItem.post.id == newItem.post.id
            }

            override fun areContentsTheSame(
                oldItem: DuckPostLoggedInWrapper,
                newItem: DuckPostLoggedInWrapper
            ): Boolean {
                return oldItem.post.author == newItem.post.author &&
                        oldItem.post.headline == newItem.post.headline &&
                        oldItem.post.image == newItem.post.image &&
                        oldItem.post.upVotes == newItem.post.upVotes &&
                        oldItem.loggedIn == newItem.loggedIn
            }
        } //define AsyncListDiffer

    init {
        mDiffer = AsyncListDiffer(this, diffCallback)
    }

    fun unregisterListener() {
        listener = null
    }

    fun updateList(list: List<DuckPostLoggedInWrapper>) {
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
        fun bind(item: DuckPostLoggedInWrapper, listener: ItemClickListener?) = with(itemView) {
            binding.titleTextView.text = item.post.headline
            Glide.with(this).load(item.post.image).into(binding.imageView)
            binding.votesTextView.text = item.post.upVotes.toString()
            if (item.loggedIn) {
                binding.upvoteButton.visibility = View.VISIBLE
                binding.downvoteButton.visibility = View.VISIBLE
            } else {
                binding.upvoteButton.visibility = View.INVISIBLE
                binding.downvoteButton.visibility = View.INVISIBLE
            }

            binding.upvoteButton.setOnClickListener {
                listener?.onUpVote(item.post)
            }

            binding.downvoteButton.setOnClickListener {
                listener?.onDownVote(item.post)
            }

            setOnClickListener {
                listener?.onItemClick(item.post)
            }

            setOnLongClickListener {
                listener?.onItemLongClick(item.post)
                return@setOnLongClickListener true
            }
        }
    }
}