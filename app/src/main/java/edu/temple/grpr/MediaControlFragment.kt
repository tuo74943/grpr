package edu.temple.grpr

import android.media.MediaRecorder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton

class MediaControlFragment : Fragment() {

    lateinit var recordButton: ImageButton
    lateinit var playButton :ImageButton
    lateinit var cancelButton : ImageButton
    lateinit var sendButton: Button
    lateinit var mediaRecorder: MediaRecorder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_media_control, container, false)

        recordButton = layout.findViewById(R.id.recordButton)
        playButton = layout.findViewById(R.id.playButton)
        cancelButton = layout.findViewById(R.id.cancelButton)
        sendButton = layout.findViewById(R.id.sendButton)


        recordButton.setOnClickListener{
            //TODO Record audio and save it to file
        }

        playButton.setOnClickListener {
            //TODO Play audio recorded from record button
        }

        cancelButton.setOnClickListener {
            //TODO Delete audio recorded and reset fragment to original state
        }

        sendButton.setOnClickListener {
            //TODO Send audio file to fcm and place it into recyclerview
        }
        return layout
    }

}