package com.example.telesignal.ui.chat.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telesignal.R
import com.example.telesignal.data.chat.dto.MessageDto

class ChatAdapter(private val dataSet: ArrayList<MessageDto>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val messageView: TextView
        val authorView: TextView

        init {
            // Define click listener for the ViewHolder's View
            messageView = view.findViewById(R.id.message_text_view)
            authorView = view.findViewById(R.id.message_author_view)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.chat_message, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.messageView.text = dataSet[position].message
        viewHolder.authorView.text = dataSet[position].username
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun addMessage(messageDto: MessageDto) {
        dataSet.add(messageDto)
        this.notifyItemInserted(dataSet.size - 1)
    }


}
