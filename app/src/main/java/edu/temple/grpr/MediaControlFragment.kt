package edu.temple.grpr

import android.media.MediaDataSource
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import java.io.File
import java.io.IOException

class MediaControlFragment : Fragment() {

    lateinit var recordButton: ImageButton
    lateinit var playButton :ImageButton
    lateinit var cancelButton : ImageButton
    lateinit var sendButton: Button

    var player: MediaPlayer? = null
    var recorder: MediaRecorder? = null
    val filename = "my_file"
    lateinit var file : File
    var isRecording = false

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

        file = File(context?.filesDir, filename)

        recordButton.setOnClickListener{
            addUI()
            isRecording = true
            startRecording()
        }

        playButton.setOnClickListener {
            //TODO Play audio recorded from record button
            startPlaying()
        }

        cancelButton.setOnClickListener {
            //TODO Delete audio recorded and reset fragment to original state
            if(isRecording) {
                stopRecording()
                isRecording = false
            }
            //removeUI()
        }

        sendButton.setOnClickListener {
            //TODO Send audio file to fcm and place it into recyclerview

            removeUI()
        }

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        removeUI()
    }

    fun removeUI(){
        playButton.visibility = View.INVISIBLE
        cancelButton.visibility = View.INVISIBLE
        sendButton.visibility = View.INVISIBLE
    }

    fun addUI(){
        playButton.visibility = View.VISIBLE
        cancelButton.visibility = View.VISIBLE
        sendButton.visibility = View.VISIBLE
    }

    private fun startRecording() {
        Toast.makeText(requireContext(), "Recording", Toast.LENGTH_SHORT).show()
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(file)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("LOG TAG", "prepare() failed")
            }
            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(file.path)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("LOG_TAG", "prepare() failed")
            }
        }
    }
}