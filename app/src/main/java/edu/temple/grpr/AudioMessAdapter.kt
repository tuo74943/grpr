package edu.temple.grpr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AudioMessAdapter(_context : Context, _list : MessageList, ocl: (AudioMessage)-> Unit) : RecyclerView.Adapter<AudioMessAdapter.AudioViewHolder>(){
    class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val usernameTV = itemView.findViewById<TextView>(R.id.usernameTextView)
        val timeTV = itemView.findViewById<TextView>(R.id.timeTextView)
        val playButton = itemView.findViewById<ImageButton>(R.id.playButton)

        fun bind(audioMessage: AudioMessage, onClick : (AudioMessage) -> Unit){
            usernameTV.text = audioMessage.username
            timeTV.text = audioMessage.time
            playButton.setOnClickListener { onClick(audioMessage) }
        }
    }

    val context = _context
    val list = _list
    val onClick = ocl

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val inflator = LayoutInflater.from(context)
        val view = inflator.inflate(R.layout.audio_message, parent, false)
        return AudioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        holder.bind(list.getMessage(position), onClick)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}