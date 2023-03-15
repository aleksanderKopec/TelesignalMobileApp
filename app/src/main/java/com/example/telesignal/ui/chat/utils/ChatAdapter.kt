package com.example.telesignal.ui.chat.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telesignal.R

class ChatAdapter(private val dataSet: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class OutViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val messageView: TextView
        val authorView: TextView

        init {
            // Define click listener for the ViewHolder's View
            messageView = view.findViewById(R.id.message_text_view)
            authorView = view.findViewById(R.id.message_author_view)
        }
    }

    class InViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val messageView: TextView
        val authorView: TextView

        init {
            // Define click listener for the ViewHolder's View
            messageView = view.findViewById(R.id.message_text_view)
            authorView = view.findViewById(R.id.message_author_view)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Create a new view, which defines the UI of the list item
        if (viewType == 0) {
            return OutViewHolder(
                    LayoutInflater.from(viewGroup.context)
                            .inflate(R.layout.chat_message, viewGroup, false)
            )
        }

        return InViewHolder(
                LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.my_chat_message, viewGroup, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataSet[position].mine) 0 else 1
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val element = dataSet[position]
        if (viewHolder is InViewHolder) {
            viewHolder.messageView.text = element.message
            viewHolder.authorView.text = element.username
        } else if (viewHolder is OutViewHolder) {
            viewHolder.messageView.text = element.message
            viewHolder.authorView.text = element.username
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun addMessage(message: Message) {
        dataSet.add(message)
        this.notifyItemInserted(dataSet.size - 1)
    }


}
