package com.jeffreyliu.duckit.adapter

import android.os.Bundle
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

    private companion object {
        private const val DIFF_DELTA_KEY_VOTES = "votes"
    }

    interface ItemClickListener {
        fun onItemClick(post: DuckPost)
        fun onItemLongClick(post: DuckPost)
        fun onUpVote(id: String)
        fun onDownVote(id: String)
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

            override fun getChangePayload(
                oldItem: DuckPostLoggedInWrapper,
                newItem: DuckPostLoggedInWrapper
            ): Any? {
                val diff = Bundle()
                if (oldItem.post.upVotes != newItem.post.upVotes) {
                    diff.putInt(DIFF_DELTA_KEY_VOTES, newItem.post.upVotes)
                }
                return if (diff.size() == 0) {
                    null
                } else diff
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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        }
        val o = payloads[0] as Bundle
        for (key in o.keySet()) {
            if (key == DIFF_DELTA_KEY_VOTES) {
                holder.bindVote(o.getInt(key))
                break
            }
        }
    }

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
                listener?.onUpVote(item.post.id)
            }

            binding.downvoteButton.setOnClickListener {
                listener?.onDownVote(item.post.id)
            }

            setOnClickListener {
                listener?.onItemClick(item.post)
            }

            setOnLongClickListener {
                listener?.onItemLongClick(item.post)
                return@setOnLongClickListener true
            }
        }

        fun bindVote(vote: Int) {
            binding.votesTextView.text = vote.toString()
        }
    }
}