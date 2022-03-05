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

class AudioMessAdapter(_context : Context, _list : ArrayList<User>, ocl: (User)-> Unit) : RecyclerView.Adapter<AudioMessAdapter.AudioViewHolder>(){
    class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val usernameTV = itemView.findViewById<TextView>(R.id.usernameTextView)
        val timeTV = itemView.findViewById<TextView>(R.id.timeTextView)
        val playButton = itemView.findViewById<ImageButton>(R.id.playButton)

        fun bind(user: User, onClick : (User) -> Unit){
            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val currentDateandTime: String = sdf.format(Date())

            usernameTV.text = user.username
            timeTV.text = currentDateandTime
            playButton.setOnClickListener { onClick(user) }
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
        holder.bind(list[position], onClick)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}