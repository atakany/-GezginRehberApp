package com.example.gezginrehberbt.ui.placedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gezginrehberbt.databinding.ItemCommentBinding
import com.example.gezginrehberbt.model.Comment
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter(private var comments: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    fun updateComments(newComments: List<Comment>) {
        comments = newComments
        notifyDataSetChanged() // Daha verimli bir yöntem için DiffUtil kullanılabilir.
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.tvUserName.text = comment.userName
            binding.ratingBarComment.rating = comment.rating
            binding.tvCommentText.text = comment.text
            
            // Tarih formatını daha okunabilir hale getir.
            comment.timestamp?.let {
                val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("tr"))
                binding.tvCommentDate.text = sdf.format(it)
            } ?: run {
                binding.tvCommentDate.text = ""
            }
        }
    }
}
