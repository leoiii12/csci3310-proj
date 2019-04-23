package com.cuhk.travelligent.page.comments

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cuhk.travelligent.R


import io.swagger.client.models.CommentDto

import kotlinx.android.synthetic.main.fragment_comment.view.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * [RecyclerView.Adapter] that can display a [CommentDto].
 */
class MyCommentRecyclerViewAdapter(
    private val values: List<CommentDto>
) : RecyclerView.Adapter<MyCommentRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.createUserNameView.text = "${item.createUser!!.firstName} ${item.createUser!!.lastName}"
        holder.contentView.text = item.content

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault())

        holder.createDateView.text = formatter.format(Instant.parse(item.createDate!!))

        with(holder.view) {
            tag = item
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val createUserNameView: TextView = view.create_user_name
        val contentView: TextView = view.content
        val createDateView: TextView = view.create_date

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}
